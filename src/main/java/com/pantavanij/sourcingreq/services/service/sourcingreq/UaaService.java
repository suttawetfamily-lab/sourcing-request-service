package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ContractDetailClientDto;
import com.pantavanij.sourcingreq.services.domain.dto.OrganizationClientDto;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import com.pantavanij.sourcingreq.services.domain.request.AuthenticationRequest;
import com.pantavanij.sourcingreq.services.domain.request.RefreshAuthenticationRequest;
import com.pantavanij.sourcingreq.services.domain.response.TenantInfoResponse;
import com.pantavanij.sourcingreq.services.domain.response.UserDetailResponse;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.*;
import java.util.List;
import java.util.Map;

public interface UaaService {
    ContractDetailClientDto getContractDetail(String tenantId, String idp, String userName, Map<String, UserDetailResponse> userDetailMap);

    Map<String, Object> getToken(AuthenticationRequest request);

    Map<String, Object> getRefreshToken(RefreshAuthenticationRequest request);

    OrganizationClientDto getOrganizationByTenantIdAndUserName(String tenantId, String idp, String userName);

    OrganizationClientDto getOrganizationByTenantIdAndUserName(String tenantId, String idp, String userName, List<Integer> excludeBorgIds);

    String getUserTimeZone(UserDto userDto, Map<String, UserDetailResponse> userDetailMap);

    UserDetailResponse getUserDetailByTenantIdAndIdpAndUserName(String tenantId, String idp, String userDetailKey, String userName, Map<String, UserDetailResponse> userDetailMap);

    List<TenantInfoResponse> getTenantInfo(String tenantIdArray);

    Map<String, Object> getTokenExchange(String refreshToken);
}
