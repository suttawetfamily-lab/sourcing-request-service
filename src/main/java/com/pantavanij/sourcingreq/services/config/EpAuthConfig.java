package com.pantavanij.sourcingreq.services.config;

import com.pantavanij.sourcingreq.services.interceptor.CustomErrorDecoder;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class EpAuthConfig {

    @Value("${epauth.auth.basic.username}")
    private String epAuthUserName;

    @Value("${epauth.auth.basic.password}")
    private String epAuthPassword;

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
        return new BasicAuthRequestInterceptor(epAuthUserName, epAuthPassword);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
