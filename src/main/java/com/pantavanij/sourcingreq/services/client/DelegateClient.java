package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.FeignClientConfig;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;

@FeignClient(value = "delegate-client", url = "${base.url.api.delegate}", configuration = FeignClientConfig.class)
public interface DelegateClient {

    String AUTH_TOKEN = "Authorization";

    @MethodExecuteTime
    @PostMapping(value = "/delegation/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    DelegationSearchResponse searchDelegation(@RequestHeader(AUTH_TOKEN) String bearerToken, @RequestBody DelegationSearchRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/delegation/delegator-active", consumes = MediaType.APPLICATION_JSON_VALUE)
    DelegatorActiveResponse getDelegatorActive(@RequestHeader(AUTH_TOKEN) String bearerToken, @RequestBody DelegatorActiveRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/delegation/active-created-date", consumes = MediaType.APPLICATION_JSON_VALUE)
    DelegationActiveCreateDateResponse getDelegationActiveCreateDate(@RequestHeader(AUTH_TOKEN) String bearerToken, @RequestBody DelegationActiveCreateDateRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/delegation/delegatee-by-delegator", produces = MediaType.APPLICATION_JSON_VALUE)
    DelegateeByDelegatorResponse getDelegateeByDelegator(@RequestHeader(AUTH_TOKEN) String bearerToken, @RequestBody @Valid DelegateeByDelegatorRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/delegation/delegator-by-delegatee", produces = MediaType.APPLICATION_JSON_VALUE)
    DelegatorByDelegateeResponse getDelegatorByDelegatee(@RequestHeader(AUTH_TOKEN) String bearerToken, @RequestBody @Valid DelegatorByDelegateeRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/delegation/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse validateDelegation(@RequestHeader(AUTH_TOKEN) String bearerToken, @RequestBody @Valid DelegationValidateRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/delegation/active", produces = MediaType.APPLICATION_JSON_VALUE)
    DelegationActiveResponse getActiveDelegationByDeletatorAndDelegatee(@RequestHeader(AUTH_TOKEN) String bearerToken, @RequestBody @Valid DelegationActiveRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/delegation/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse saveDelegation(@RequestHeader(AUTH_TOKEN) String bearerToken, @RequestBody DelegationRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/delegation/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse cancelDelegation(@RequestHeader(AUTH_TOKEN) String bearerToken, @RequestBody DelegationCancelRequest request);

}
