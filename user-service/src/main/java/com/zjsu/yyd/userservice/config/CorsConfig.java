//package com.zjsu.yyd.userservice.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        // 支持 localhost + 任意端口、宿主机局域网 IP（Docker 下访问）
//                        .allowedOriginPatterns(
//                                "http://localhost:*",
//                                "http://127.0.0.1:*",
//                                "http://192.168.*.*"
//                        )
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedHeaders("*")
//                        .allowCredentials(true)
//                        .maxAge(3600); // 预检请求缓存 1 小时
//            }
//        };
//    }
//}
