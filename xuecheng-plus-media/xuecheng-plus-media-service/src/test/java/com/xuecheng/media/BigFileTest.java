package com.xuecheng.media;


import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Arrays;
//大文件处理测试
public class BigFileTest {

    //测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourceFile = new File("D:\\study\\language\\java\\video\\1-1nacos.mp4");
        //分块文件存储路径
        String chunkPath = "D:\\study\\language\\java\\video\\chunk\\";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        //分块大小
        //第一个1024是1KB，乘以第二个1024，也就是1024KB,也就是1MB，最后乘以5，就是5MB
        long chunkSize = 1024 * 1024 * 5;
        //分块数量
        //cell向上取整
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        System.out.println("分块总数：" + chunkNum);
        //缓冲区大小
        byte[] b = new byte[1024];
        //使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");

         // 分块处理循环，根据分块数量 chunkNum 执行多次
        for (int i = 0; i < chunkNum; i++) {
            // 创建每个分块文件（例如：chunkPath = "/tmp/chunk_", i=0 时就是 /tmp/chunk_0）
            File file = new File(chunkPath + i);
            // 如果目标分块文件已存在，则先删除，确保是新的干净文件
            if (file.exists()) {
                file.delete();
            }
            // 创建新的空分块文件
            boolean newFile = file.createNewFile();
            // 如果文件成功创建，才执行写入操作
            if (newFile) {
                // 创建 RandomAccessFile 对象用于写入（"rw" 表示读写模式）
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;  // 用于记录每次读取的字节数
                // 从原始文件读取数据到缓冲区 b 中，并写入当前分块文件
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);  // 把读取的 b 中的 len 个字节写入分块文件

                    // 如果当前分块文件已达到预设大小 chunkSize，就停止写入，开始下一个分块
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }

                // 写入完毕后关闭该分块文件的写入流
                raf_write.close();

                // 输出当前分块完成提示
                System.out.println("完成分块" + i);
            }
        }

        raf_read.close();

    }


    //测试文件合并方法
    @Test
    public void testMerge() throws IOException {
        // 1️⃣ 块文件所在的目录，即之前分割保存的小文件的文件夹
        File chunkFolder = new File("D:\\study\\language\\java\\video\\chunk\\");

        // 2️⃣ 原始完整视频文件，用来对比校验合并结果是否一致
        File originalFile = new File("D:\\study\\language\\java\\video\\1-1nacos.mp4");

        // 3️⃣ 合并输出文件，也就是最终合并好的大文件
        File mergeFile = new File("D:\\study\\language\\java\\video\\1-1nacos_2.mp4");

        // 如果合并文件已存在，先删除旧文件，避免冲突
        if (mergeFile.exists()) {
            mergeFile.delete();
        }

        // 创建一个新的空的合并文件
        mergeFile.createNewFile();

        // 创建 RandomAccessFile 用于向合并文件写入内容（"rw" 表示可读可写）
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");

        // 设置写入指针从文件开头开始
        raf_write.seek(0);

        // 定义一个缓冲区，每次最多读写 1024 字节（1KB）
        byte[] b = new byte[1024];

        // 获取所有的分块文件（即目录下的所有文件）
        File[] fileArray = chunkFolder.listFiles();

        // 将文件数组转换为列表，方便排序
        List<File> fileList = Arrays.asList(fileArray);

        // 根据文件名（数字）进行升序排序，例如：0, 1, 2, 3……
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                // 假设文件名就是数字字符串，比如 "0", "1", "2"...
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });

        // 4️⃣ 遍历排序后的分块文件，依次读取并写入到合并文件中
        for (File chunkFile : fileList) {
            // 打开每个分块文件进行读取（"rw" 虽然是读写，但这里只用读）
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;  // 每次读取的字节数
            // 一次读取 1024 字节到缓冲区 b 中，直到读取完为止
            while ((len = raf_read.read(b)) != -1) {
                // 将读取到的数据写入合并文件
                raf_write.write(b, 0, len);
            }
            // 关闭读取流，避免资源泄露
            raf_read.close();
        }

        // 写入完成后，关闭写入流
        raf_write.close();

        // 5️⃣ 使用 MD5 校验合并后的文件是否和原始文件一致
        try (
                FileInputStream fileInputStream = new FileInputStream(originalFile);       // 原始文件流
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);          // 合并后文件流
        ) {
            // 读取原始文件的 MD5 值
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);

            // 读取合并文件的 MD5 值
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);

            // 对比两个 MD5 值，如果一致说明合并成功
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }
        }
    }

}
