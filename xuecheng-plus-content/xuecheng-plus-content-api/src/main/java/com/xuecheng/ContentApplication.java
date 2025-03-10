package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ycz
 * @version 1.0
 * @description 内容管理服务启动类
 * @date 2025/3/5
 */
@EnableSwagger2Doc
@SpringBootApplication


public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
