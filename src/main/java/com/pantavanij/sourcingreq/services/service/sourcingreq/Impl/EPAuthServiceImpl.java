package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthDefaultUserDTO;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthUserDTO;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.MailingConfigRequest;
import com.pantavanij.sourcingreq.services.domain.request.UpdateSessionRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.EPAuthService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EPAuthServiceImpl implements EPAuthService {

    private static final Logger logger = LoggerFactory.getLogger(EPAuthServiceImpl.class);
    private final EpAuthClient epAuthClient;

    @Override
    public EPAuthUserListResponse getListByConditions(EPAuthUserSearchRequest request, String privilegeCode) {
        String[] privilegeCodeList = new String[] { privilegeCode };
        return epAuthClient.getEPAuthUserList(privilegeCodeList, request);
    }

    @Override
    public EPAuthUserDTO getDefaultReportLine(String tenantName, String borgId, String sysUserId) {
        return epAuthClient.getEPAuthUser(tenantName, borgId, sysUserId);
    }

    @Override
    public EPAuthDefaultUserDTO getDefaultUser(String tenantName, String borgId, String sysUserId) {
        return epAuthClient.getEPAuthDefaultUser(tenantName, borgId, sysUserId);
    }

    @Override
    public List<PurchaserGroupResponse> getAllPurchaserGroup(String tenantCode) {
        return epAuthClient.getAllPurchaserGroup(tenantCode);
    }

    @Override
    public List<ApprovalHierarchyResponse> getAllApprovalHierarchy(String tenantCode) {
        return epAuthClient.getAllApprovalHierarchy(tenantCode);
    }

    @Override
    public MailingConfigResponse getSRMailingConfig(MailingConfigRequest request) {
        return epAuthClient.getSRMailingConfig(request);
    }

    @Override
    public VerifySessionResponse updateSession(UpdateSessionRequest request) {
        return epAuthClient.updateSession(request);
    }

    @Override
    public DeleteSessionResponse deleteUserSession(String username, String tenantId) {
        return epAuthClient.deleteUserSession(username, tenantId);
    }
}
