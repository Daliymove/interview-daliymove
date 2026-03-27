package com.daliymove.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Daliymove 应用程序主启动类
 * - 启用 Spring Boot 自动配置
 * - 扫描 com.daliymove 包下的所有组件
 * - 扫描 MyBatis Mapper 接口
 * - 启用异步处理支持
 */
@SpringBootApplication
@ComponentScan("com.daliymove")
@MapperScan({"com.daliymove.system.mapper", "com.daliymove.modules.chat.mapper"})
@EnableAsync
public class DaliymoveApplication {

    /** 应用程序主入口方法 */
    public static void main(String[] args) {
        SpringApplication.run(DaliymoveApplication.class, args);
    }
}