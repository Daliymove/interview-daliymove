package com.daliymove.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.daliymove.admin.mapper")
@EnableAsync
public class DaliymoveAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaliymoveAdminApplication.class, args);
    }
}