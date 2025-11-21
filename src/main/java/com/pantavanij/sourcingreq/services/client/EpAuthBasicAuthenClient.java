package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.EpAuthConfig;
import com.pantavanij.sourcingreq.services.config.FeignClientConfig;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthDefaultUserDTO;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthUserDTO;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.AppiniResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthUserListResponse;
import com.pantavanij.sourcingreq.services.domain.response.PurchaserGroupResponse;
import com.pantavanij.sourcingreq.services.domain.response.user.BorgUserResponse;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "ep-auth-basic-authen-client", url = "${base.url.api.epauth}", configuration = EpAuthConfig.class)
public interface EpAuthBasicAuthenClient {

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/appini/{tenantCode}")
    AppiniResponse getSessionTimeout(@PathVariable("tenantCode") String tenantCode
            , @Valid @RequestParam("appName") String appName
            , @Valid @RequestParam("name") String name
            , @Valid @RequestParam("section") String section);
}
