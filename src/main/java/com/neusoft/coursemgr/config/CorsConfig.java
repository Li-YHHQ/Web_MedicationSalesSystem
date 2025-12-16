package com.neusoft.coursemgr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许所有源（生产环境建议指定具体域名）
        configuration.addAllowedOriginPattern("*");
        // 允许所有头
        configuration.addAllowedHeader("*");
        // 允许所有方法（GET, POST, PUT, DELETE, OPTIONS）
        configuration.addAllowedMethod("*");
        // 允许携带凭证（cookies, authorization headers）
        configuration.setAllowCredentials(true);
        // 预检请求缓存时间（秒）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用 CORS 配置
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }
}
