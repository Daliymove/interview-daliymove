package com.daliymove.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置
 * - 配置 API 文档基本信息
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 基本信息
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Daliymove API")
                        .description("面试移动端后端服务API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Daliymove")
                                .email("admin@example.com")));
    }
}