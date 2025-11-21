package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.client.UaaBffClient;
import com.pantavanij.sourcingreq.services.client.UaaClient;
import com.pantavanij.sourcingreq.services.domain.dto.BorgUserAdditionalDto;
import com.pantavanij.sourcingreq.services.domain.dto.ContractDetailClientDto;
import com.pantavanij.sourcingreq.services.domain.dto.OrganizationClientDto;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.AuthenticationRequest;
import com.pantavanij.sourcingreq.services.domain.request.RefreshAuthenticationRequest;
import com.pantavanij.sourcingreq.services.domain.response.TenantInfoResponse;
import com.pantavanij.sourcingreq.services.domain.response.UserDetailResponse;
import com.pantavanij.sourcingreq.services.domain.response.user.BorgUserResponse;
import com.pantavanij.sourcingreq.services.enums.Role;
import com.pantavanij.sourcingreq.services.exception.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UaaServiceImpl implements UaaService {
    private static final Logger logger = LoggerFactory.getLogger(UaaServiceImpl.class);

    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final UaaClient uaaClient;
    private final UaaBffClient uaaBffClient;
    private final EpAuthClient epAuthClient;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public ContractDetailClientDto getContractDetail(String tenantId, String idp, String userName, Map<String, UserDetailResponse> userDetailMap) {
        ContractDetailClientDto contractDetailClientDto = null;
        UserDetailResponse userDetail = null;
        try {
            String userDetailKey = String.format("%s-%s", tenantId, userName);
            userDetail = getUserDetailByTenantIdAndIdpAndUserName(tenantId, idp, userDetailKey, userName, userDetailMap);
        } catch (Exception ex) {
            log.info("GetUserDetail Exception for User : "+ userName + " with exception : "+ex.getLocalizedMessage());
        }
        if (userDetail != null) {
            contractDetailClientDto = new ContractDetailClientDto();
            contractDetailClientDto.setUserId(userDetail.getUserId());
            contractDetailClientDto.setUsername(userDetail.getUsername());
            contractDetailClientDto.setFirstName(userDetail.getFirstName());
            contractDetailClientDto.setLastName(userDetail.getLastName() != null ? userDetail.getLastName() : "");
            contractDetailClientDto.setEmail(userDetail.getEmail());
            contractDetailClientDto.setMobilePhone(userDetail.getMobilePhone());
            contractDetailClientDto.setMobile(userDetail.getMobilePhone());
            contractDetailClientDto.setPhone(userDetail.getPhone());
            contractDetailClientDto.setDepartment(userDetail.getDepartment());
            contractDetailClientDto.setHomeBorgID(userDetail.getHomeBorgID());
            contractDetailClientDto.setHomeBorgName(userDetail.getHomeBorgName());
            contractDetailClientDto.setBorgIdList(userDetail.getBorgIdList());
        }
        return contractDetailClientDto;
    }

    @Override
    public Map<String, Object> getToken(AuthenticationRequest request) {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", request.getGrantType());
        formParams.put("auth_code", request.getAuthCode());
        return uaaBffClient.authenticate(formParams);
    }

    @Override
    public Map<String, Object> getRefreshToken(RefreshAuthenticationRequest request) {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", request.getGrantType());
        formParams.put("refresh_token", request.getRefreshToken());
        Map<String, Object> authenInfo = uaaBffClient.authenticate(formParams);

        if(authenInfo.get("auth_code") != null) {
            UserDto userDto = jwtTokenUtil.parseToken(authenInfo.get("refresh_token").toString());
            boolean isPurchaser = StringUtils.isNotEmpty(getPrivilegeScope(Role.PURCHASER.privilegeCode(), userDto));
            if(isPurchaser) {
                String tenantCode = userDto.getTenantId();
                Tenant tenant = tenantService.findByCode(tenantCode);
                String newAuthCode = authenInfo.get("auth_code").toString();
                String currentAuthCode = tenantConfigService.getERFXAuthCode(tenant.getRecId());
                if(currentAuthCode == null || !currentAuthCode.equalsIgnoreCase(newAuthCode)) {
                    tenantConfigService.setERFXAuthCode(tenant.getRecId(), newAuthCode);
                }
            }
        }
        return authenInfo;
    }

    @Override
    public OrganizationClientDto getOrganizationByTenantIdAndUserName(String tenantId, String idp, String userName) {
        return getOrganizationByTenantIdAndUserName(tenantId, idp, userName, null);
    }

    @Override
    public OrganizationClientDto getOrganizationByTenantIdAndUserName(
            String tenantId, String idp, String userName, List<Integer> excludeBorgIds) {

        OrganizationClientDto organizationClientDto = null;
        List<BorgUserResponse> borgUserList =
                uaaClient.getBorgUserByTenantIdAndIdpAndUserName(tenantId, idp, userName);

        if (borgUserList != null && !borgUserList.isEmpty()) {

            List<BorgUserAdditionalDto> borgList = new ArrayList<>();

            // ✅ borgUserList: ถ้าไม่มี exclude → ไม่กรองเลย
            List<BorgUserResponse> filteredUserList = (excludeBorgIds != null && !excludeBorgIds.isEmpty())
                    ? borgUserList.stream().filter(b -> !excludeBorgIds.contains(b.getBorgID())).collect(Collectors.toList())
                    : borgUserList;

            for (BorgUserResponse borgUser : filteredUserList) {
                BorgUserAdditionalDto dto = new BorgUserAdditionalDto();
                dto.setBorgID(borgUser.getBorgID());
                dto.setBorgCode(borgUser.getBorgCode());
                dto.setBorgName(borgUser.getBorgName());
                borgList.add(dto);
            }

            Tenant tenant = tenantService.findByCode(tenantId);
            boolean canViewBorgAll = tenantConfigService.getBorgAllConfig(tenant.getRecId());

            if (canViewBorgAll) {
                List<BorgUserResponse> borgAllList = epAuthClient.getBorgAllListByTenantId(tenantId);
                if (borgAllList != null && !borgAllList.isEmpty()) {

                    // ✅ ใช้ exclude ถ้ามี, ถ้าไม่มี fallback [0,1]
                    List<Integer> excludeList = (excludeBorgIds != null && !excludeBorgIds.isEmpty())
                            ? excludeBorgIds
                            : Arrays.asList(0, 1);

                    borgAllList = borgAllList.stream()
                            .filter(b -> !excludeList.contains(b.getBorgID()))
                            .collect(Collectors.toList());

                    for (BorgUserResponse borg : borgAllList) {
                        BorgUserAdditionalDto dto = new BorgUserAdditionalDto();
                        dto.setBorgID(borg.getBorgID());
                        dto.setBorgCode(borg.getBorgCode());
                        dto.setBorgName(borg.getBorgName());
                        borgList.add(dto);
                    }
                }
            }

            // ✅ ตัดซ้ำ & เรียงชื่อ
            borgList = borgList.stream()
                    .sorted(Comparator.comparing(BorgUserAdditionalDto::getBorgName))
                    .distinct()
                    .collect(Collectors.toList());

            if (!borgList.isEmpty()) {
                organizationClientDto = new OrganizationClientDto();
                organizationClientDto.setBorgUserList(borgList);
            }
        }

        return organizationClientDto;
    }





    @Override
    @Cacheable(value="userTimeZone", key="{#userDto}")
    public String getUserTimeZone(UserDto userDto, Map<String, UserDetailResponse> userDetailMap) {
        String userName = userDto.getUsername();
        String tenantId = userDto.getTenantId();
        String idp = userDto.getIdp();

        Tenant tenant = tenantService.findByCode(tenantId);
        String userDetailKey = String.format("%s-%s", tenant.getRecId(), userName);
        UserDetailResponse userDetail = getUserDetailByTenantIdAndIdpAndUserName(tenantId, idp, userDetailKey, userName, userDetailMap);

        return userDetail != null ? userDetail.getTimeZone() : null;
    }

    @Override
    public UserDetailResponse getUserDetailByTenantIdAndIdpAndUserName(String tenantId, String idp, String userDetailKey, String userName, Map<String, UserDetailResponse> userDetailMap) {
        try {
            UserDetailResponse userDetailResponse;
            if(userName != null && !userName.isEmpty()) {
                if(userDetailMap != null && userDetailMap.containsKey(userDetailKey)) {
                    userDetailResponse = userDetailMap.get(userDetailKey);
                } else {
                    userDetailResponse = uaaClient.getUserDetailByTenantIdAndIdpAndUserName(tenantId, idp, userName);
                    if(userDetailMap != null) userDetailMap.put(userDetailKey, userDetailResponse);
                }
                return userDetailResponse;
            } else {
                return null;
            }
        } catch (DataNotFoundException ex) {
            return null;
        }
    }

    @Override
    public List<TenantInfoResponse> getTenantInfo(String tenantIdArray) {
        return uaaClient.getTenantInfo(tenantIdArray);
    }

    @Override
    public Map<String, Object> getTokenExchange(String refreshToken) {
        try {
            // Body
            Map<String, Object> formParams = new HashMap<>();
            formParams.put("grant_type", Constant.EXCHANGE_TOKEN);
            formParams.put("jwt", refreshToken);

            // Params
            String idp = Constant.IDP_EP;
            return uaaBffClient.tokenExchange(formParams, idp);
        } catch (Exception e) {
            logger.error("Error while getTokenExchange {}", e.getMessage());
            throw new BadRequestException("Error while getTokenExchange");
        }
    }

    public String getPrivilegeScope(String privilegeCode, UserDto userDto) {
        if (userDto != null) {
            String privilegeScope = userDto.getPrivilegeScope().get(privilegeCode.toUpperCase());
            return null == privilegeScope ? "" : privilegeScope;
        } else {
            return "";
        }
    }
}
