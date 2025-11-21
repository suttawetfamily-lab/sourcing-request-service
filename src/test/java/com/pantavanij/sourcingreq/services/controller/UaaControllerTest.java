package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.ContractDetailClientDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.OrganizationClientDto;
import com.pantavanij.sourcingreq.services.domain.request.AuthenticationRequest;
import com.pantavanij.sourcingreq.services.domain.request.RefreshAuthenticationRequest;
import com.pantavanij.sourcingreq.services.domain.request.UpdateSessionRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.EPAuthService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UaaControllerTest {

    @Mock
    private UaaService uaaService;

    @Mock
    private EPAuthService epAuthService;

    @InjectMocks
    private UaaController uaaController;

    private static final String MOCK_TENANT_ID = "1";
    private static final String MOCK_IDP = "EP";
    private static final String MOCK_USERNAME = "testUser";

    @Before
    public void setUp() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn(MOCK_TENANT_ID);
            appUtilMock.when(AppUtil::getIdp).thenReturn(MOCK_IDP);
            appUtilMock.when(AppUtil::getUserName).thenReturn(MOCK_USERNAME);
            appUtilMock.when(AppUtil::isRequester).thenReturn(true);
            appUtilMock.when(AppUtil::isPurchaser).thenReturn(false);
            appUtilMock.when(AppUtil::isReviewer).thenReturn(false);
            appUtilMock.when(AppUtil::isReportLine).thenReturn(false);
        }
    }

    @Test
    public void getToken_WhenValidCredentials_ReturnsToken() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", "mock-token");
        when(uaaService.getToken(request)).thenReturn(tokenResponse);

        // Act
        ResponseEntity response = uaaController.getToken(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void getToken_WhenInvalidCredentials_ReturnsUnauthorized() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        when(uaaService.getToken(request)).thenReturn(new HashMap<>());

        // Act
        ResponseEntity response = uaaController.getToken(request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiErrorResponse);
    }

    @Test
    public void getRefreshToken_WhenValidToken_ReturnsNewToken() {
        // Arrange
        RefreshAuthenticationRequest request = new RefreshAuthenticationRequest();
        Map<String, Object> refreshResponse = new HashMap<>();
        refreshResponse.put("access_token", "new-token");
        when(uaaService.getRefreshToken(request)).thenReturn(refreshResponse);

        // Act
        ResponseEntity response = uaaController.getRefreshToken(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void getContractDetail_ReturnsContractDetails() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Setup static mocks
            appUtilMock.when(AppUtil::getTenantId).thenReturn(MOCK_TENANT_ID);
            appUtilMock.when(AppUtil::getIdp).thenReturn(MOCK_IDP);
            appUtilMock.when(AppUtil::getUserName).thenReturn(MOCK_USERNAME);
            appUtilMock.when(AppUtil::isRequester).thenReturn(true);
            appUtilMock.when(AppUtil::isPurchaser).thenReturn(false);
            appUtilMock.when(AppUtil::isReviewer).thenReturn(false);
            appUtilMock.when(AppUtil::isReportLine).thenReturn(false);

            // Arrange
            ContractDetailClientDto mockDto = new ContractDetailClientDto();
            when(uaaService.getContractDetail(
                    eq(MOCK_TENANT_ID),
                    eq(MOCK_IDP),
                    eq(MOCK_USERNAME),
                    isNull()
            )).thenReturn(mockDto);

            // Act
            ResponseEntity<ApiResponse<ContractDetailClientDto>> response = uaaController.getContractDetail();

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            ContractDetailClientDto resultDto = response.getBody().getData();
            assertNotNull(resultDto);
            assertTrue(resultDto.isRequester());
            assertFalse(resultDto.isPurchaser());
            assertFalse(resultDto.isReviewer());
            assertFalse(resultDto.isReportLine());
        }
    }



//    @Test
//    public void getOrganization_ReturnsOrganizationList() {
//        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
//            // Setup static mocks
//            appUtilMock.when(AppUtil::getTenantId).thenReturn(MOCK_TENANT_ID);
//            appUtilMock.when(AppUtil::getIdp).thenReturn(MOCK_IDP);
//            appUtilMock.when(AppUtil::getUserName).thenReturn(MOCK_USERNAME);
//
//            // Arrange
//            OrganizationClientDto mockOrgDto = new OrganizationClientDto();
//            when(uaaService.getOrganizationByTenantIdAndUserName(
//                    eq(MOCK_TENANT_ID),
//                    eq(MOCK_IDP),
//                    eq(MOCK_USERNAME)
//            )).thenReturn(mockOrgDto);
//
//            // Act
//            ResponseEntity<ApiResponse<List<OptionDto>>> response = uaaController.getOrganization();
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertNotNull(response.getBody());
//        }
//    }

    @Test
    public void checkSessionExpired_ReturnsVerificationStatus() {
        // Arrange
        UpdateSessionRequest request = new UpdateSessionRequest();
        VerifySessionResponse mockResponse = new VerifySessionResponse();
        when(epAuthService.updateSession(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<VerifySessionResponse> response = uaaController.checkSessionExpired(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void deleteSession_ReturnsSuccessResponse() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Setup static mocks
            appUtilMock.when(AppUtil::getTenantId).thenReturn(MOCK_TENANT_ID);
            appUtilMock.when(AppUtil::getUserName).thenReturn(MOCK_USERNAME);

            // Arrange
            DeleteSessionResponse mockResponse = new DeleteSessionResponse();
            when(epAuthService.deleteUserSession(MOCK_USERNAME, MOCK_TENANT_ID)).thenReturn(mockResponse);

            // Act
            ResponseEntity<DeleteSessionResponse> response = uaaController.deleteSession();

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(mockResponse, response.getBody());
        }
    }
}