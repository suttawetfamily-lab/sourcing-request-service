package com.pantavanij.sourcingreq.services.config;

import com.pantavanij.sourcingreq.services.converter.StringToMasterGroupConverter;
import com.pantavanij.sourcingreq.services.converter.StringToPathUrlConverter;
import com.pantavanij.sourcingreq.services.interceptor.InterceptLog;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    public static final String DATA_JSON = "data-json";

    private final InterceptLog logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("DELETE", "GET", "OPTIONS", "PATCH", "POST", "PUT")
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .exposedHeaders(DATA_JSON);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToPathUrlConverter());
        registry.addConverter(new StringToMasterGroupConverter());
    }
}
