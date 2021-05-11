package com.oyome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    public CorsConfig() {
    }
    @Bean
    public CorsFilter corsFilter(){
        //1.添加cors配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.addAllowedOrigin("http://localhost:8080");
//        corsConfiguration.addAllowedOrigin("http://120.27.24.50:8080");
//        corsConfiguration.addAllowedOrigin("http://47.111.9.249:8080");
//        corsConfiguration.addAllowedOrigin("http://www.oyome.cn:8080");
//
//        corsConfiguration.addAllowedOrigin("http://192.168.22.214:8090");
//
//        corsConfiguration.addAllowedOrigin("http://localhost:8081");
        corsConfiguration.addAllowedOrigin("*");

        //设置是否发送cookie信息
        corsConfiguration.setAllowCredentials(true);
        //设置允许请求的方式
        corsConfiguration.addAllowedMethod("*");
        //设置允许的header
        corsConfiguration.addAllowedHeader("*");
        //2.为url添加映射路径
        UrlBasedCorsConfigurationSource urlSource = new UrlBasedCorsConfigurationSource();
        urlSource.registerCorsConfiguration("/**",corsConfiguration);
        //3.返回重新定义好的corsSource
        return new CorsFilter(urlSource);
    }
}
