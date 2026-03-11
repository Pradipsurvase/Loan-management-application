package com.example.educationloan.config;


import com.example.educationloan.interceptor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final RateLimitingTokenBucketInterceptor rateLimitingTokenBucketInterceptor;
    private final TokenExpiryInterceptor tokenExpiryInterceptor;
    private final AuditLoggingInterceptor auditLoggingInterceptor;
    //private final RateLimitingFixedWindowInterceptor fixedWindowRateLimiter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(requestLoggingInterceptor).addPathPatterns("/**");

        registry.addInterceptor(rateLimitingTokenBucketInterceptor).addPathPatterns("/**");

        registry.addInterceptor(tokenExpiryInterceptor).addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/refresh");

        registry.addInterceptor(auditLoggingInterceptor).addPathPatterns("/**");

       // registry.addInterceptor(fixedWindowRateLimiter).addPathPatterns("/**").excludePathPatterns("/actuator/health", "/actuator/info", "/favicon.ico", "/error");


    }
}
