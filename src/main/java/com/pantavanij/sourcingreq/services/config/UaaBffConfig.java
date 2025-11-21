package com.pantavanij.sourcingreq.services.config;

import com.pantavanij.sourcingreq.services.interceptor.CustomErrorDecoder;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class UaaBffConfig {

    @Value("${uaa.auth.basic.username}")
    private String uaaUserName;

    @Value("${uaa.auth.basic.password}")
    private String uaaPassword;

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(uaaUserName, uaaPassword);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
