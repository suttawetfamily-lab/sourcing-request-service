package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.AsgardClientConfig;
import com.pantavanij.sourcingreq.services.domain.response.admin.AdminResponse;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "admin-client", url = "${base.url.api.admin}", configuration = AsgardClientConfig.class)
public interface AdminClient {

    @MethodExecuteTime
    @GetMapping(value = "/api/v1/master-data-project/current-data-list", consumes = MediaType.APPLICATION_JSON_VALUE)
    AdminResponse getProjectList();
}
