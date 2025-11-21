package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.FeignClientConfig;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthDefaultUserDTO;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthUserDTO;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.MailingConfigRequest;
import com.pantavanij.sourcingreq.services.domain.request.UpdateSessionRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.domain.response.user.BorgUserResponse;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "ep-auth-client", url = "${base.url.api.epauth}", configuration = FeignClientConfig.class)
public interface EpAuthClient {

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/borgAll?tenantId={tenantCode}", produces = "application/json")
    List<BorgUserResponse> getBorgAllListByTenantId(@PathVariable("tenantCode") String tenantCode);

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.POST, value = "/user/search?borgId=&privilegeCode={privilegeCode}", produces = "application/json")
    EPAuthUserListResponse getEPAuthUserList(@PathVariable("privilegeCode") String[] privilegeCode,
                                             @RequestBody EPAuthUserSearchRequest request);

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/user/defaultreportline/{tenantName}/{borgId}/{sysUserId}")
    EPAuthUserDTO getEPAuthUser(@PathVariable("tenantName") String tenantName, @PathVariable("borgId") String borgId, @PathVariable("sysUserId") String sysUserId);

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/user/defaultreportline/{tenantName}/{borgId}/{sysUserId}")
    EPAuthDefaultUserDTO getEPAuthDefaultUser(@PathVariable("tenantName") String tenantName, @PathVariable("borgId") String borgId, @PathVariable("sysUserId") String sysUserId);

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/user/allpurchasergroup/{tenantCode}")
    List<PurchaserGroupResponse> getAllPurchaserGroup(@PathVariable("tenantCode") String tenantCode);

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/user/all-approval-hierarchy/{tenantCode}")
    List<ApprovalHierarchyResponse> getAllApprovalHierarchy(@PathVariable("tenantCode") String tenantCode);

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.POST, value = "/sr-mailing-config")
    MailingConfigResponse getSRMailingConfig(@RequestBody @Valid MailingConfigRequest request);

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.POST, value = "/user/update-session")
    VerifySessionResponse updateSession(@RequestBody UpdateSessionRequest request);

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.DELETE, value = "/user/delete-user-session")
    DeleteSessionResponse deleteUserSession(@Valid @RequestParam("username") String username,
                                            @Valid @RequestParam("tenantId") String tenantId);
}
