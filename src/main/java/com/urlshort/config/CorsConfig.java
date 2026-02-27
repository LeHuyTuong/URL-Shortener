package com.urlshort.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow React dev server and Vercel production
        // Sử dụng allowedOriginPatterns để hỗ trợ regex cho Vercel preview URLs
        config.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",                                    // Local dev (bất kỳ port nào)
            "https://url-shortener-tuong.vercel.app",               // Production chính
            "https://url-shortener-tuong-*.vercel.app"              // Vercel preview deployments
        ));

        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials
        config.setAllowCredentials(true);
        
        // Max age for preflight cache
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
