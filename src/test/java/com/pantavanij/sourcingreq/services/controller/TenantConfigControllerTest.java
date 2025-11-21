package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingMenuDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingRequestDisplayDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenantConfigControllerTest {
    private TenantConfigService tenantConfigService = mock(TenantConfigService.class);
    private TenantService tenantService = mock(TenantService.class);
    private UaaService uaaService = mock(UaaService.class);
    private AttachmentService attachmentService;
    private TenantConfigController tenantConfigController = new TenantConfigController(tenantConfigService, tenantService, uaaService, attachmentService);

//    @Test
//    public void getWorkflowTemplate_success() {
//        String tenantCode = "1";
//        String mockWorkFlowTemplate = "14582";
//
//        when(tenantService.findByCode(tenantCode)).thenReturn(Tenant.builder().recId(1).build());
//        when(tenantConfigService.getWorkflowTemplateId(Integer.valueOf(tenantCode))).thenReturn(mockWorkFlowTemplate);
//
//        ResponseEntity actualResult;
//        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
//            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
//            actualResult = tenantConfigController.getWorkflowTemplate();
//        }
//
//        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(mockWorkFlowTemplate));
//
//        assertEquals(expectedResult, actualResult);
//    }

//    @Test
//    public void getWorkflowTemplate_notFound() {
//        String tenantCode = "1";
//
//        when(tenantService.findByCode(tenantCode)).thenReturn(Tenant.builder().recId(1).build());
//        when(tenantConfigService.getWorkflowTemplateId(Integer.valueOf(tenantCode))).thenReturn(null);
//
//        ResponseEntity actualResult;
//        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
//            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
//            actualResult = tenantConfigController.getWorkflowTemplate();
//        }
//
//        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7027, ApiMessage.E7027.description())));
//
//        assertEquals(expectedResult, actualResult);
//    }

//    @Test
//    public void getWorkflowTemplate_tenantNotFound() {
//        String tenantCode = "1";
//
//        when(tenantService.findByCode(tenantCode)).thenReturn(null);
//
//        Exception actualResult;
//        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
//            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
//            actualResult = assertThrows(BusinessException.class, () -> tenantConfigController.getWorkflowTemplate());
//        }
//
//        assertEquals("E7016: Tenant is not found", actualResult.getMessage());
//    }

    @Test
    public void getMaxSessionTimeout_success() {
        String tenantCode = "1";
        Long mockMaxSessionTimeout = 30L;

        when(tenantService.findByCode(tenantCode)).thenReturn(Tenant.builder().recId(1).build());
        when(tenantConfigService.getMaxSessionTimeout(Integer.valueOf(tenantCode))).thenReturn(mockMaxSessionTimeout);

        ResponseEntity actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = tenantConfigController.getMaxSessionTimeout();
        }

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(mockMaxSessionTimeout));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getMaxSessionTimeout_notFound() {
        String tenantCode = "1";

        when(tenantService.findByCode(tenantCode)).thenReturn(Tenant.builder().recId(1).build());
        when(tenantConfigService.getMaxSessionTimeout(Integer.valueOf(tenantCode))).thenReturn(null);

        ResponseEntity actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = tenantConfigController.getMaxSessionTimeout();
        }

        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7027, ApiMessage.E7027.description())));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getMaxSessionTimeout_tenantNotFound() {
        String tenantCode = "1";

        when(tenantService.findByCode(tenantCode)).thenReturn(null);

        Exception actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = assertThrows(BusinessException.class, () -> tenantConfigController.getMaxSessionTimeout());
        }

        assertEquals("E7016: Tenant is not found", actualResult.getMessage());
    }

    @Test
    public void getLogoImageUrl_success() {
        String tenantCode = "1";
        String mockLogoUrl = "https://ep.pantavanij.com/images/logo/true.svg";

        when(tenantService.findByCode(tenantCode)).thenReturn(Tenant.builder().recId(1).build());
        when(tenantConfigService.getSRLogoImageFileId(Integer.valueOf(tenantCode))).thenReturn(mockLogoUrl);

        ResponseEntity actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = tenantConfigController.getLogoImage("sr");
        }

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(mockLogoUrl));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getLogoImageUrl_notFound() {
        String tenantCode = "1";

        when(tenantService.findByCode(tenantCode)).thenReturn(Tenant.builder().recId(1).build());
        when(tenantConfigService.getSRLogoImageFileId(Integer.valueOf(tenantCode))).thenReturn(null);

        ResponseEntity actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = tenantConfigController.getLogoImage("sr");
        }

        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7027, ApiMessage.E7027.description())));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getLogoImageUrl_tenantNotFound() {
        String tenantCode = "1";

        when(tenantService.findByCode(tenantCode)).thenReturn(null);

        Exception actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = assertThrows(BusinessException.class, () -> tenantConfigController.getLogoImage("sr"));
        }

        assertEquals("E7016: Tenant is not found", actualResult.getMessage());
    }

    @Test
    public void tenantIsValid_getDisplayConfiguration_success() {
        String tenantCode = "TRUE";
        Integer tenantId = 1;
        Tenant mockTenant = Tenant.builder().recId(tenantId).code(tenantCode).build();

        SourcingMenuDto mockSourcingMenuDto = SourcingMenuDto.builder()
                .sourcingMenuId(1L)
                .name("Existing Price")
                .build();

        SourcingRequestDisplayDto mockSourcingRequestDisplayDto = SourcingRequestDisplayDto.builder()
                .tenantId(tenantCode)
                .isShowExistingPriceCreateSourcing(false)
                .existingPriceSourcingMenu(Collections.singletonList(mockSourcingMenuDto))
                .isShowStep3(true)
                .build();

        when(tenantService.findByCode(tenantCode)).thenReturn(mockTenant);
        when(tenantConfigService.getDisplayConfiguration(mockTenant)).thenReturn(mockSourcingRequestDisplayDto);

        ResponseEntity<ApiResponse<SourcingRequestDisplayDto>> actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = tenantConfigController.getDisplayConfiguration();
        }

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(mockSourcingRequestDisplayDto));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void tenantIsNull_getDisplayConfiguration_userNotAllowed() {
        String tenantCode = "TRUE";

        when(tenantService.findByCode(tenantCode)).thenReturn(null);

        ResponseEntity<ApiResponse<SourcingRequestDisplayDto>> actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = tenantConfigController.getDisplayConfiguration();
        }

        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7064, ApiMessage.E7064.description())));

        assertEquals(expectedResult, actualResult);
    }

}