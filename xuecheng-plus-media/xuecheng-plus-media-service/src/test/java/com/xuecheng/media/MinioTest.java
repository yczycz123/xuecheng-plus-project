package com.xuecheng.media;


import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Filter;

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

        if (source_md5.equals(local_md5)){
            System.out.println("下载成功");
        }


    }
}
