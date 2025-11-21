package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.TenantRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantSearchRequest;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenantControllerTest {

    @InjectMocks
    private TenantController tenantController;

    @Mock
    private TenantService tenantService;

    @Mock
    private UaaService uaaService;

    @Mock
    private UserDto mockUser;

    private static final String TEST_TENANT_CODE = "TEST_TENANT";
    private static final String TEST_USER = "testUser";
    private static final String TEST_TIMEZONE = "Asia/Bangkok";

    @Before
    public void setup() {
        try (MockedStatic<AppUtil> appUtil = mockStatic(AppUtil.class)) {
            appUtil.when(AppUtil::getTenantId).thenReturn(TEST_TENANT_CODE);
            appUtil.when(AppUtil::getUser).thenReturn(mockUser);
        }

        mockUser = new UserDto();
        mockUser.setUsername(TEST_USER);
    }

    @Test
    public void getTenant_Success() {
        Tenant tenant = Tenant.builder()
                .recId(1)
                .code(TEST_TENANT_CODE)
                .name("Test Tenant")
                .description("test description")
                .createdBy("testUser")
                .createdDate(DateTimeUtil.getTimestampUTC())
                .build();

        when(tenantService.findByCode(TEST_TENANT_CODE)).thenReturn(tenant);

        try (MockedStatic<AppUtil> appUtil = mockStatic(AppUtil.class)) {
            appUtil.when(AppUtil::getTenantId).thenReturn(TEST_TENANT_CODE);

            ResponseEntity<?> response = tenantController.getTenant();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(tenantService).findByCode(TEST_TENANT_CODE);
            assertNotNull(response.getBody());
        }
    }

    @Test
    public void getTenant_NotFound() {
        ResponseEntity<?> response = tenantController.getTenant();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void viewTenant_Success() {
        TenantDto tenantDto = new TenantDto();
        tenantDto.setTenantId(1L);
        tenantDto.setTenantCode("TEST_CODE");
        tenantDto.setTenantName("Test Tenant");

        try (MockedStatic<AppUtil> appUtil = mockStatic(AppUtil.class)) {
            appUtil.when(AppUtil::getUser).thenReturn(mockUser);
            appUtil.when(AppUtil::getTenantId).thenReturn(TEST_TENANT_CODE);

            when(uaaService.getUserTimeZone(mockUser, null)).thenReturn(TEST_TIMEZONE);
            when(tenantService.getTenantDtoByRecId(1, TEST_TIMEZONE)).thenReturn(tenantDto);

            ResponseEntity response = tenantController.viewTenant(1);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test
    public void searchTenant_Success() {
        TenantSearchRequest request = new TenantSearchRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setSortBy("code");
        request.setSortOrder("desc");

        // Create a complete TenantDto
        TenantDto tenantDto = new TenantDto();
        tenantDto.setTenantId(1L);
        tenantDto.setTenantCode("TEST_CODE");
        tenantDto.setTenantName("Test Tenant");

        List<TenantDto> tenantList = new ArrayList<>();
        tenantList.add(tenantDto);

        // Create a complete TenantSearchDto
        TenantSearchDto searchDto = TenantSearchDto.builder()
                .tenantList(tenantList)
                .total(1L)
                .totalPage(1)
                .page(1)
                .pageSize(10)
                .build();

        // Mock static methods
        try (MockedStatic<AppUtil> appUtil = mockStatic(AppUtil.class)) {
            appUtil.when(AppUtil::getUser).thenReturn(mockUser);
            appUtil.when(AppUtil::getTenantId).thenReturn(TEST_TENANT_CODE);

            // Mock service calls
            when(uaaService.getUserTimeZone(mockUser, null)).thenReturn(TEST_TIMEZONE);
            when(tenantService.searchTenantListByCondition(eq(request), any(PageRequest.class), eq(TEST_TIMEZONE)))
                    .thenReturn(searchDto);

            ResponseEntity response = tenantController.searchTenant(request);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test
    public void createTenant_Success() {
        TenantRequest request = new TenantRequest();
        when(tenantService.createTenant(request)).thenReturn(1);

        ResponseEntity response = tenantController.createTenant(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void updateTenant_Success() {
        TenantRequest request = new TenantRequest();
        request.setRecId(1);
        when(tenantService.updateTenant(request)).thenReturn(1);

        ResponseEntity response = tenantController.updateTenant(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
