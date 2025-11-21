package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.FeignClientConfig;
import com.pantavanij.sourcingreq.services.domain.dto.EMailConfigDto;
import com.pantavanij.sourcingreq.services.domain.dto.EMailDto;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "email-client", url = "${email.service.host}", configuration = FeignClientConfig.class)
public interface EmailClient {

    @MethodExecuteTime
    @PostMapping(value = "/api/send", produces = "application/json")
    EMailDto sendEmail(@RequestBody EMailConfigDto request);
}
