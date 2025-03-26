package com.xuecheng.media;


import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Filter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//测试minio的sdk
public class MinioTest {


    // 创建 MinioClient 实例，替换为你的 MinIO 服务器地址、Access Key 和 Secret Key
    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.101.65:9000") // MinIO 服务器地址
            .credentials("minioadmin", "minioadmin") // 你的 Access Key 和 Secret Key
            .build();


    //上传文件
    @Test
    void test_upload() {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
//                    .object("test001.mp4")
                    .object("001/test01.mp4")//添加子目录，并设置上传后的对象名
                    .filename("D:\\study\\language\\java\\video\\1-1、Nacos的下载与启动-480P 清晰-AVC.mp4")  //本地文件路径
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }


    //删除文件
    @Test
    void test_delete() {
        try {
            // 设置删除文件的参数
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket("testbucket")  // 存储桶名称
                    .object("001/test01.mp4")  // 要删除的文件对象名（包括路径）
                    .build();

            // 删除文件
            minioClient.removeObject(removeObjectArgs);
            System.out.println("文件删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件删除失败");
        }
    }


    //下载文件
    @Test
    public void test_getFile() throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("001/test01.mp4").build();
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);

        //指定输出流
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\Download\\101.mp4"));

        IOUtils.copy(inputStream, outputStream);
        //校验文件的一致性完整性。对文件的内容进行md5

        //原始文件
        String source_md5 = DigestUtils.md5Hex(new FileInputStream(new File("D:\\study\\language\\java\\video\\1-1、Nacos的下载与启动-480P 清晰-AVC.mp4")));

        //下载下来的本地文件
        String local_md5 = DigestUtils.md5Hex(new FileInputStream(new File("D:\\Download\\101.mp4")));

        if (source_md5.equals(local_md5)) {
            System.out.println("下载成功");
        }


    }

    //将分块文件上传至minio
    @Test
    public void uploadChunk() {
        // 1️⃣ 指定本地分块文件夹的路径
        String chunkFolderPath = "D:\\study\\language\\java\\video\\chunk\\";

        // 2️⃣ 创建 File 对象表示该目录
        File chunkFolder = new File(chunkFolderPath);

        // 3️⃣ 获取目录中所有的分块文件（返回 File 数组）
        File[] files = chunkFolder.listFiles();

        // 4️⃣ 遍历每个分块文件，依次上传到 MinIO 对象存储
        for (int i = 0; i < files.length; i++) {
            try {
                // 构建 MinIO 上传参数对象
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                        .bucket("testbucket")                     // 上传目标桶的名称（必须存在）
                        .object("chunk/" + i)                     // 对象名（在桶里的路径），这里是 chunk/0, chunk/1...
                        .filename(files[i].getAbsolutePath())     // 本地文件绝对路径（上传源文件）
                        .build();

                // 5️⃣ 使用 MinIO 客户端执行上传操作
                minioClient.uploadObject(uploadObjectArgs);

                // 6️⃣ 成功上传后打印提示
                System.out.println("上传分块成功" + i);
            } catch (Exception e) {
                // 如果某个分块上传失败，打印错误堆栈信息，继续下一个
                e.printStackTrace();
            }
        }
    }


    //调用minio接口合并文件，要求分块文件最小5M
    @Test
    public void test_merge() throws Exception {
        // 1️⃣ 构建一个 List<ComposeSource>，表示要合并的分块对象来源（chunk/0 ~ chunk/5）
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i) // 生成从 0 开始的整数流
                .limit(3)                                              // 限制为前 13 个
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")                              // 分块对象所在的 bucket 名
                        .object("chunk/" + i)      // 分块对象名，如 chunk/0、chunk/1 ...
                        .build())
                .collect(Collectors.toList());                         // 最终收集为 List<ComposeSource>
        // 2️⃣ 构建一个 ComposeObjectArgs 对象，表示要执行“合并对象”的操作
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")           // 合并后的文件要放在哪个 bucket 中
                .object("merge01.mp4")          // 合并后的对象名称（相当于远程文件名）
                .sources(sources)               // 指定所有要合并的源对象（顺序很重要）
                .build();
        // 3️⃣ 使用 MinIO 客户端执行合并操作（服务端完成，速度快）
        minioClient.composeObject(composeObjectArgs);
    }

    //清除分块文件
    @Test
    public void test_removeObjects() {
        // 1️⃣ 构建要删除的对象列表（DeleteObject），对应 chunk/0 到 chunk/5
        List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i) // 生成从 0 开始的整数流
                .limit(6)                                                   // 限制为前 6 个（0 ~ 5）
                .map(i -> new DeleteObject("chunk/".concat(Integer.toString(i)))) // 拼接对象名 "chunk/0" ~ "chunk/5"
                .collect(Collectors.toList());                              // 收集为 List<DeleteObject>
        // 2️⃣ 构建批量删除参数对象（指定桶和对象列表）
        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                .bucket("testbucket")               // 要删除的对象所在桶名
                .objects(deleteObjects)             // 要删除的对象列表
                .build();
        // 3️⃣ 调用 MinIO 的批量删除方法，返回一个包含删除结果的可迭代对象
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        // 4️⃣ 遍历删除结果，处理每个删除失败的情况
        results.forEach(r -> {
            DeleteError deleteError = null;
            try {
                // 如果某个删除操作失败，会抛异常
                deleteError = r.get(); // 调用 get() 会触发异常信息
            } catch (Exception e) {
                e.printStackTrace(); // 打印错误堆栈，方便排查
            }
        });
    }

}
