package com.pantavanij.sourcingreq.services.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ERFXConfig {

    @Value("${erfx.service.host}")
    private String hostName;

    @Value("${base.url.ptvn.cookie}")
    String BASEURL_PTVN_COOKIE;

}