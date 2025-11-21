package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.UaaBffConfig;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "uaa-bff-client", url = "${base.url.api.uaa-bff}", configuration = UaaBffConfig.class)
public interface UaaBffClient {

    @MethodExecuteTime
    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Map<String, Object> authenticate(@RequestBody Map<String, ?> formParams);

    @MethodExecuteTime
    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Map<String, Object> tokenExchange(@RequestBody Map<String, ?> formParams, @RequestParam("idp") String idp);
}
