package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.PurchaserService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PurchaserControllerTest {

    @InjectMocks
    private PurchaserController purchaserController;

    @Mock
    private PurchaserService purchaserService;

    @Mock
    private TenantService tenantService;

    private static final String TENANT_CODE = "ait";

    @Before
    public void setup() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn(TENANT_CODE);
        }
    }

    @Test
    public void getPurchaserBySearchTerm_Success() {
        // Given
        String searchTerm = "test";
        Integer categoryId = 1;
        Tenant tenant = new Tenant();
        tenant.setRecId(1);
        List<OptionDetailDto> expectedOptions = new ArrayList<>();

        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn(TENANT_CODE);
            when(tenantService.findByCode(TENANT_CODE)).thenReturn(tenant);
            when(purchaserService.getPurchaserByTenantIdAndSearchTermAndCategoryId(tenant.getRecId(), searchTerm, categoryId))
                    .thenReturn(expectedOptions);

            // When
            ResponseEntity<ApiResponse<List<OptionDetailDto>>> response = purchaserController.getPurchaserBySearchTerm(searchTerm, categoryId);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(purchaserService).getPurchaserByTenantIdAndSearchTermAndCategoryId(tenant.getRecId(), searchTerm, categoryId);
        }
    }


    @Test
    public void viewPurchaser_Success() {
        // Given
        Integer purchaserId = 1;
        PurchaserDto expectedDto = new PurchaserDto();
        when(purchaserService.findPurchaserByRecId(purchaserId)).thenReturn(expectedDto);

        // When
        ResponseEntity<?> response = purchaserController.viewPurchaser(purchaserId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(purchaserService).findPurchaserByRecId(purchaserId);
    }

    @Test
    public void createPurchaser_Success() {
        // Given
        PurchaserRequest request = new PurchaserRequest();
        PurchaserDto expectedDto = new PurchaserDto();
        expectedDto.setRecId(1);
        when(purchaserService.createPurchaser(request)).thenReturn(expectedDto);

        // When
        ResponseEntity<?> response = purchaserController.createPurchaser(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(purchaserService).createPurchaser(request);
    }

    @Test
    public void searchPurchaser_Success() {
        // Given
        PurchaserSearchRequest request = new PurchaserSearchRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setSortBy("purchaserName");
        request.setSortOrder("desc");

        PurchaserSearchDto searchDto = new PurchaserSearchDto();
        searchDto.setPurchaserList(new ArrayList<>());
        searchDto.setTotal(1L);
        searchDto.setTotalPage(1);
        searchDto.setPageSize(10);

        when(purchaserService.searchPurchaserByCondition(any(PurchaserSearchRequest.class), any(Pageable.class)))
                .thenReturn(searchDto);

        // When
        ResponseEntity<?> response = purchaserController.searchPurchaser(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(purchaserService).searchPurchaserByCondition(any(PurchaserSearchRequest.class), any(Pageable.class));
    }

    @Test
    public void updatePurchaser_Success() {
        // Given
        PurchaserRequest request = new PurchaserRequest();
        PurchaserDto expectedDto = new PurchaserDto();
        when(purchaserService.updatePurchaser(request)).thenReturn(expectedDto);

        // When
        ResponseEntity<?> response = purchaserController.updatePurchaser(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(purchaserService).updatePurchaser(request);
    }

    @Test
    public void deletePurchaser_Success() {
        // Given
        Integer purchaserId = 1;
        when(purchaserService.deletePurchaserByRecId(purchaserId)).thenReturn(true);

        // When
        ResponseEntity<?> response = purchaserController.deletePurchaser(purchaserId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(purchaserService).deletePurchaserByRecId(purchaserId);
    }

    @Test
    public void updatePurchaserSequence_Success() {
        // Given
        SequenceRequest request = new SequenceRequest();
        PurchaserDto expectedDto = new PurchaserDto();
        when(purchaserService.updatePurchaserSequence(request)).thenReturn(expectedDto);

        // When
        ResponseEntity<?> response = purchaserController.updatePurchaserSequence(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(purchaserService).updatePurchaserSequence(request);
    }
}
