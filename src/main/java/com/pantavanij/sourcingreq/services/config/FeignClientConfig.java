package com.pantavanij.sourcingreq.services.config;

import com.pantavanij.sourcingreq.services.interceptor.CustomErrorDecoder;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;

public class FeignClientConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
