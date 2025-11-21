package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthDefaultUserDTO;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthUserDTO;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.MailingConfigRequest;
import com.pantavanij.sourcingreq.services.domain.request.UpdateSessionRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EPAuthServiceImplTest {

    @Mock
    private EpAuthClient epAuthClient;

    @InjectMocks
    private EPAuthServiceImpl epAuthService;

    private EPAuthUserSearchRequest searchRequest;
    private String privilegeCode;
    private String tenantName;
    private String borgId;
    private String sysUserId;

    @Before
    public void setup() {
        searchRequest = new EPAuthUserSearchRequest();
        privilegeCode = "TEST_PRIVILEGE";
        tenantName = "TEST_TENANT";
        borgId = "TEST_BORG";
        sysUserId = "TEST_USER";
    }

    @Test
    public void getListByConditions_ShouldReturnUserList() {
        EPAuthUserListResponse expectedResponse = new EPAuthUserListResponse();
        when(epAuthClient.getEPAuthUserList(any(String[].class), eq(searchRequest)))
            .thenReturn(expectedResponse);

        EPAuthUserListResponse result = epAuthService.getListByConditions(searchRequest, privilegeCode);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void getDefaultReportLine_ShouldReturnUserDTO() {
        EPAuthUserDTO expectedUser = new EPAuthUserDTO();
        when(epAuthClient.getEPAuthUser(tenantName, borgId, sysUserId))
            .thenReturn(expectedUser);

        EPAuthUserDTO result = epAuthService.getDefaultReportLine(tenantName, borgId, sysUserId);

        assertNotNull(result);
        assertEquals(expectedUser, result);
    }

    @Test
    public void getDefaultUser_ShouldReturnDefaultUserDTO() {
        EPAuthDefaultUserDTO expectedUser = new EPAuthDefaultUserDTO();
        when(epAuthClient.getEPAuthDefaultUser(tenantName, borgId, sysUserId))
            .thenReturn(expectedUser);

        EPAuthDefaultUserDTO result = epAuthService.getDefaultUser(tenantName, borgId, sysUserId);

        assertNotNull(result);
        assertEquals(expectedUser, result);
    }

    @Test
    public void getAllPurchaserGroup_ShouldReturnPurchaserGroups() {
        List<PurchaserGroupResponse> expectedGroups = Arrays.asList(new PurchaserGroupResponse());
        when(epAuthClient.getAllPurchaserGroup(tenantName))
            .thenReturn(expectedGroups);

        List<PurchaserGroupResponse> result = epAuthService.getAllPurchaserGroup(tenantName);

        assertNotNull(result);
        assertEquals(expectedGroups, result);
    }

    @Test
    public void getAllApprovalHierarchy_ShouldReturnHierarchyList() {
        List<ApprovalHierarchyResponse> expectedHierarchy = Arrays.asList(new ApprovalHierarchyResponse());
        when(epAuthClient.getAllApprovalHierarchy(tenantName))
            .thenReturn(expectedHierarchy);

        List<ApprovalHierarchyResponse> result = epAuthService.getAllApprovalHierarchy(tenantName);

        assertNotNull(result);
        assertEquals(expectedHierarchy, result);
    }

    @Test
    public void getSRMailingConfig_ShouldReturnMailingConfig() {
        MailingConfigRequest request = new MailingConfigRequest();
        MailingConfigResponse expectedConfig = new MailingConfigResponse();
        when(epAuthClient.getSRMailingConfig(request))
            .thenReturn(expectedConfig);

        MailingConfigResponse result = epAuthService.getSRMailingConfig(request);

        assertNotNull(result);
        assertEquals(expectedConfig, result);
    }

    @Test
    public void updateSession_ShouldReturnVerifySessionResponse() {
        UpdateSessionRequest request = new UpdateSessionRequest();
        VerifySessionResponse expectedResponse = new VerifySessionResponse();
        when(epAuthClient.updateSession(request))
            .thenReturn(expectedResponse);

        VerifySessionResponse result = epAuthService.updateSession(request);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void deleteUserSession_ShouldReturnDeleteSessionResponse() {
        DeleteSessionResponse expectedResponse = new DeleteSessionResponse();
        when(epAuthClient.deleteUserSession(sysUserId, tenantName))
            .thenReturn(expectedResponse);

        DeleteSessionResponse result = epAuthService.deleteUserSession(sysUserId, tenantName);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }
}
