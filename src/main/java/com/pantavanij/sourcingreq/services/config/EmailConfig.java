package com.pantavanij.sourcingreq.services.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EmailConfig {

    @Value("${email.service.host}")
    private String emailHost;

}