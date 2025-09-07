package com.acm.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 요청에 대해
                .allowedOrigins("*") // Swagger를 포함한 외부 도메인에서 접근 허용
                .allowedMethods("*") // GET, POST, PUT, DELETE 등 허용
                .allowedHeaders("*");
    }
}
