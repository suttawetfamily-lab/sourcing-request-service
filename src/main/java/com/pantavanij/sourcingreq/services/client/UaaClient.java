package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.FeignClientConfig;
import com.pantavanij.sourcingreq.services.domain.response.TenantInfoResponse;
import com.pantavanij.sourcingreq.services.domain.response.UserDetailResponse;
import com.pantavanij.sourcingreq.services.domain.response.user.BorgUserResponse;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "uaa-client", url = "${uaa.service.host}", configuration = FeignClientConfig.class)
public interface UaaClient {

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/api/tenant/{tenantCode}/idp/{idp}/borg/{username}", produces = "application/json")
    List<BorgUserResponse> getBorgUserByTenantIdAndIdpAndUserName(@PathVariable("tenantCode") String tenantCode,
                                                                  @PathVariable("idp") String idp,
                                                                  @PathVariable("username") String username);

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/api/tenant/{tenantCode}/idp/{idp}/user/{username}", produces = "application/json")
    UserDetailResponse getUserDetailByTenantIdAndIdpAndUserName(@PathVariable("tenantCode") String tenantCode,
                                                                @PathVariable("idp") String idp,
                                                                @PathVariable("username") String username);


    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/api/tenants/info", produces = "application/json")
    List<TenantInfoResponse> getTenantInfo(@RequestParam(name = "tenantId") String tenantIdArray);
}
