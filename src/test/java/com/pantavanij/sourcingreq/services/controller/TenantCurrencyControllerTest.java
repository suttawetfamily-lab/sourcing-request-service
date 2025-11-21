package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantCurrencyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TenantCurrencyControllerTest {

    @Mock
    private TenantCurrencyService tenantCurrencyService;

    @InjectMocks
    private TenantCurrencyController tenantCurrencyController;

    private TenantCurrencyDto mockTenantCurrencyDto;
    private CurrencyRequest mockCurrencyRequest;
    private TenantCurrencySearchRequest mockSearchRequest;
    private SequenceRequest mockSequenceRequest;

    @Before
    public void setup() {
        mockTenantCurrencyDto = new TenantCurrencyDto();

        mockCurrencyRequest = new CurrencyRequest();
        mockCurrencyRequest.setId(1);

        mockSearchRequest = new TenantCurrencySearchRequest();
        mockSearchRequest.setPage(1);
        mockSearchRequest.setPageSize(10);
        mockSearchRequest.setSortBy("id");
        mockSearchRequest.setSortOrder("desc");

        mockSequenceRequest = new SequenceRequest();
        mockSequenceRequest.setRecId(1);
    }

    @Test
    public void viewTenantCurrency_Success() {
        when(tenantCurrencyService.getByCurrencyId(1)).thenReturn(mockTenantCurrencyDto);

        ResponseEntity response = tenantCurrencyController.viewTenantCurrency(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void viewTenantCurrency_NotFound() {
        when(tenantCurrencyService.getByCurrencyId(1)).thenReturn(null);

        ResponseEntity response = tenantCurrencyController.viewTenantCurrency(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void searchTenantCurrency_Success() {
        TenantCurrencySearchDto searchDto = new TenantCurrencySearchDto();
        searchDto.setTotal(1L);
        searchDto.setTotalPage(1);
        searchDto.setTenantCurrencyList(Arrays.asList(mockTenantCurrencyDto));

        when(tenantCurrencyService.searchTenantCurrencyByCondition(any(), any(Pageable.class)))
                .thenReturn(searchDto);

        ResponseEntity response = tenantCurrencyController.searchTenantCurrency(mockSearchRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createTenantCurrency_Success() {
        when(tenantCurrencyService.createTenantCurrency(any())).thenReturn(1);

        ResponseEntity response = tenantCurrencyController.createTenantCurrency(mockCurrencyRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createTenantCurrency_NotFound() {
        when(tenantCurrencyService.createTenantCurrency(any())).thenReturn(0);

        ResponseEntity response = tenantCurrencyController.createTenantCurrency(mockCurrencyRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createTenantCurrency_Conflict() {
        when(tenantCurrencyService.createTenantCurrency(any())).thenReturn(-1);

        ResponseEntity response = tenantCurrencyController.createTenantCurrency(mockCurrencyRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void updateTenantCurrency_Success() {
        when(tenantCurrencyService.updateTenantCurrency(any())).thenReturn(1);

        ResponseEntity response = tenantCurrencyController.updateTenantCurrency(mockCurrencyRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void updateTenantCurrency_NotFound() {
        when(tenantCurrencyService.updateTenantCurrency(any())).thenReturn(0);

        ResponseEntity response = tenantCurrencyController.updateTenantCurrency(mockCurrencyRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateTenantCurrencySequence_Success() {
        when(tenantCurrencyService.updateTenantCurrencySequence(any())).thenReturn(mockTenantCurrencyDto);

        ResponseEntity response = tenantCurrencyController.updateTenantCurrencySequence(mockSequenceRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateTenantCurrencySequence_Error() {
        when(tenantCurrencyService.updateTenantCurrencySequence(any())).thenReturn(null);

        ResponseEntity response = tenantCurrencyController.updateTenantCurrencySequence(mockSequenceRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
