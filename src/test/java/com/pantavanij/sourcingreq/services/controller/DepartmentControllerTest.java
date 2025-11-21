package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.DepartmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.DepartmentOptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.DepartmentResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DepartmentService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DepartmentControllerTest {

    private DepartmentService departmentService = mock(DepartmentService.class);
    private TenantService tenantService = mock(TenantService.class);
    private DepartmentController departmentController = new DepartmentController(departmentService, tenantService);

    @Test
    public void getDepartmentBySearchTerm_success() {
        String tenantCode = "TRUE";
        String searchTerm = "ABC";
        Integer organizationId = 1;

        List<DepartmentDto> mockDepartmentDtoList = Arrays.asList(
                new DepartmentDto()
        );

        when(departmentService.getDepartmentBySearchTerm(searchTerm, organizationId)).thenReturn(mockDepartmentDtoList);

        ResponseEntity actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = departmentController.getDepartmentBySearchTermV1(searchTerm, organizationId);
        }

        ResponseEntity expectedResult = ResponseEntity.ok().body(new DepartmentResponse(mockDepartmentDtoList));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getDepartmentBySearchTerm_notFound() {
        String tenantCode = "TRUE";
        String searchTerm = "ABC";
        Integer organizationId = 1;

        when(departmentService.getDepartmentBySearchTerm(searchTerm, organizationId)).thenReturn(Collections.emptyList());

        ResponseEntity actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = departmentController.getDepartmentBySearchTermV1(searchTerm, organizationId);
        }

        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(ApiMessage.E7025, ApiMessage.E7025.description()));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getgDepartmentBySearchTerm_success() {
        int tenantId = 1;
        String tenantCode = "TRUE";
        String searchTerm = "pro";
        Integer organizationId = 1;

        Tenant mockTenant = Tenant.builder().recId(tenantId).code(tenantCode).build();
        List<DepartmentOptionDto> departmentOptions = Arrays.asList(
                DepartmentOptionDto.builder().value("2").name("gDepartmentA").label("gDepartment A").build(),
                DepartmentOptionDto.builder().value("3").name("gDepartmentB").label("gDepartment B").build()
        );

        when(tenantService.findByCode(tenantCode)).thenReturn(mockTenant);
        when(departmentService.getDepartmentBySearchTerm(tenantId, searchTerm, organizationId)).thenReturn(departmentOptions);

        ResponseEntity actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = departmentController.getDepartmentBySearchTerm(searchTerm, organizationId);
        }

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(departmentOptions));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getgDepartmentBySearchTerm_tenantNotFound() {
        String tenantCode = "TRUE";
        String searchTerm = "pro";
        Integer organizationId = 1;

        when(tenantService.findByCode(tenantCode)).thenReturn(null);

        Exception actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = assertThrows(BusinessException.class, () -> departmentController.getDepartmentBySearchTerm(searchTerm, organizationId));
        }

        assertEquals("E7016: Tenant is not found", actualResult.getMessage());
    }

}