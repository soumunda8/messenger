package com.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8086", "http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(MAX_AGE_SECS);
            }
        };
    }

    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String path = "C:\\Uploads" + File.separator;

        registry
                .addResourceHandler("/upload/**")
                .addResourceLocations("file:///" + path);

    }

}