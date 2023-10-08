package com.kob.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口
                .allowCredentials(true)  // 是否发送cookies
                .allowedHeaders("*")
                .allowedOriginPatterns("*")  // 支持域
                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")  // 支持方法
                .exposedHeaders("*");
    }
}
