//package com.zjsu.yyd.course.gatewayservice.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//@Configuration
//public class GatewayCorsConfig {
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true); // 允许发送 Cookie / Token
//        config.addAllowedOrigin("http://localhost:5500"); // 前端地址
//        config.addAllowedHeader("*"); // 允许所有请求头
//        config.addAllowedMethod("*"); // 允许所有请求方式（GET, POST, PUT, DELETE, OPTIONS）
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsWebFilter(source);
//    }是
//}