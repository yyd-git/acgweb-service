package com.zjsu.yyd.acgproductservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 文件上传根目录，从配置文件读取
     * application-local.yml / application-docker.yml 中配置 file.upload-dir
     */
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 封面映射到 /cover/**
        registry.addResourceHandler("/cover/**")
                .addResourceLocations("file:" + uploadDir + "/cover/")
                .setCachePeriod(3600);

        // 资源映射到 /resource/**
        registry.addResourceHandler("/resource/**")
                .addResourceLocations("file:" + uploadDir + "/resource/")
                .setCachePeriod(3600);
    }
}
