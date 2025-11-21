package com.pantavanij.sourcingreq.services.config.db;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EPConfig {

    public static String url;

    @Value("${AUTHENTICATION_EP}")
    public void setPrivateName(String url) {
        EPConfig.url = url;
    }

}
