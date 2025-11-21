package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.BifrostConfig;
import com.pantavanij.sourcingreq.services.domain.dto.ProjectActiveDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestDto;
import com.pantavanij.sourcingreq.services.domain.request.pr.CreateOraclePrRequest;
import com.pantavanij.sourcingreq.services.domain.request.supplier.CreateSupplierRequest;
import com.pantavanij.sourcingreq.services.domain.response.CreateSupplierResponse;
import com.pantavanij.sourcingreq.services.domain.response.PurchaseRequisitionResponse;
import com.pantavanij.sourcingreq.services.domain.response.SupplierResponse;
import com.pantavanij.sourcingreq.services.domain.response.pr.CreateOraclePrResponse;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "bifrost-client", url = "${bifrost.service.host}", configuration = BifrostConfig.class)
public interface BifrostClient {

    @MethodExecuteTime
    @PostMapping(value = "/{uri}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ProjectActiveDto getProjectActiveAllList(@PathVariable("uri") String uri);

    @MethodExecuteTime
    @PostMapping(value = "/{uri}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PurchaseRequisitionResponse createPR(@PathVariable("uri") String uri,
                                         @RequestBody RequestDto request);

    @MethodExecuteTime
    @PostMapping(value = "/{uri}", consumes = MediaType.APPLICATION_JSON_VALUE)
    SupplierResponse getOracleSupplier(@PathVariable("uri") String uri);

    @MethodExecuteTime
    @PostMapping(value = "/{uri}", consumes = MediaType.APPLICATION_JSON_VALUE)
    CreateSupplierResponse createOracleSupplier(@PathVariable("uri") String uri,
                                                @RequestBody CreateSupplierRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/{uri}", consumes = MediaType.APPLICATION_JSON_VALUE)
    CreateOraclePrResponse createOraclePR(@PathVariable("uri") String uri,
                                          @RequestBody CreateOraclePrRequest request);
}
