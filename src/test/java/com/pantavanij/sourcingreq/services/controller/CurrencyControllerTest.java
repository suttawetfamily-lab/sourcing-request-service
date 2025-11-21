package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.CurrencyService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
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

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private CurrencyController currencyController;

    private static final String TENANT_CODE = "TEST_TENANT";

    @Before
    public void setup() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn(TENANT_CODE);
        }
    }

    @Test
    public void getCurrencyBySearchTerm_Success() {
        // Set up test data
        Tenant tenant = Tenant.builder()
            .recId(1)
            .code(TENANT_CODE)
            .name("Test Tenant")
            .build();

        List<OptionDto> expectedOptions = List.of(
            OptionDto.builder()
                .value("USD")
                .label("US Dollar")
                .build()
        );

        // Configure mocks with try-with-resources for static mock
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn(TENANT_CODE);
            when(tenantService.findByCode(TENANT_CODE)).thenReturn(tenant);
            when(currencyService.getCurrencyByTenantIdAndSearchTerm(tenant.getRecId(), "USD"))
                .thenReturn(expectedOptions);

            // Execute and verify
            ResponseEntity<?> response = currencyController.getCurrencyBySearchTerm("USD");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            // Verify mock interactions
            verify(tenantService).findByCode(TENANT_CODE);
            verify(currencyService).getCurrencyByTenantIdAndSearchTerm(tenant.getRecId(), "USD");
        }
    }


    @Test
    public void getCurrencyById_Success() {
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setValue("USD");
        currencyDto.setName("USD");
        currencyDto.setLabel("USD Dollar");

        when(currencyService.getCurrencyById(1)).thenReturn(currencyDto);

        ResponseEntity<?> response = currencyController.getCurrencyByTenantId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void getAllCurrency_Success() {
        List<CurrencyDto> currencies = List.of(new CurrencyDto());
        when(currencyService.getAllCurrency()).thenReturn(currencies);

        ResponseEntity<?> response = currencyController.getAllCurrency();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getCurrencyByTenantIds_Success() {
        // Arrange
        Integer currencyId = 1;
        CurrencyMasterDto currencyMasterDto = new CurrencyMasterDto();
        when(currencyService.getCurrencyMasterDataById(currencyId)).thenReturn(currencyMasterDto);

        // Act
        ResponseEntity response = currencyController.getCurrencyByTenantIds(currencyId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
        assertEquals(currencyMasterDto, ((ApiResponse<?>) response.getBody()).getData());
        verify(currencyService).getCurrencyMasterDataById(currencyId);
    }

    @Test
    public void getCurrencyByTenantIds_NotFound() {
        // Arrange
        Integer currencyId = 1;
        when(currencyService.getCurrencyMasterDataById(currencyId)).thenReturn(null);

        // Act
        ResponseEntity response = currencyController.getCurrencyByTenantIds(currencyId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponseStatus);
        ApiResponseStatus status = (ApiResponseStatus) response.getBody();
        assertEquals("E7096", status.getCode());
        assertEquals(String.format(ApiMessage.E7096.description(), "Currency"), status.getDescription());
        verify(currencyService).getCurrencyMasterDataById(currencyId);
    }

    @Test
    public void searchCurrency_Success() {
        CurrencySearchRequest request = new CurrencySearchRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setSortBy("code");
        request.setSortOrder("desc");

        CurrencySearchDto searchDto = new CurrencySearchDto();
        searchDto.setCurrencyList(List.of(new CurrencyMasterDto()));
        searchDto.setTotal(1L);
        searchDto.setTotalPage(1);

        when(currencyService.searchCurrencyListByCondition(any(), any())).thenReturn(searchDto);

        ResponseEntity<?> response = currencyController.searchCurrency(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createCurrency_Success() {
        CurrencyMasterDataRequest request = new CurrencyMasterDataRequest();
        when(currencyService.createCurrency(request)).thenReturn(1);

        ResponseEntity<?> response = currencyController.createCurrency(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void updateCurrency_Success() {
        CurrencyMasterDataRequest request = new CurrencyMasterDataRequest();
        when(currencyService.updateCurrency(request)).thenReturn(1);

        ResponseEntity<?> response = currencyController.updateCurrency(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteCurrency_Success() {
        when(currencyService.deleteCurrencyById(1)).thenReturn(1);

        ResponseEntity<?> response = currencyController.deleteCurrency(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteCurrency_NotFound() {
        when(currencyService.deleteCurrencyById(1)).thenReturn(0);

        ResponseEntity<?> response = currencyController.deleteCurrency(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void searchCurrency_NoResults() {
        CurrencySearchRequest request = new CurrencySearchRequest();
        request.setPage(1);
        request.setPageSize(10);

        CurrencySearchDto searchDto = new CurrencySearchDto();
        searchDto.setCurrencyList(Collections.emptyList());

        when(currencyService.searchCurrencyListByCondition(any(), any())).thenReturn(searchDto);

        ResponseEntity<?> response = currencyController.searchCurrency(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}