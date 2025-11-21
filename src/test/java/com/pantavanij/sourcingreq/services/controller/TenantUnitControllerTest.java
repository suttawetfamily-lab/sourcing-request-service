package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantUnitService;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TenantUnitControllerTest {

    @Mock
    private TenantUnitService tenantUnitService;

    @InjectMocks
    private TenantUnitController tenantUnitController;

    private TenantUnitDto mockTenantUnitDto;
    private UnitSearchRequest mockSearchRequest;
    private UnitRequest mockUnitRequest;
    private CurrencyRequest mockCurrencyRequest;
    private SequenceRequest mockSequenceRequest;

    @Before
    public void setup() {
        mockTenantUnitDto = new TenantUnitDto();
        mockTenantUnitDto.setUnitId(1);

        mockSearchRequest = new UnitSearchRequest();
        mockSearchRequest.setPage(1);
        mockSearchRequest.setPageSize(10);
        mockSearchRequest.setSortBy("id");
        mockSearchRequest.setSortOrder("desc");

        mockUnitRequest = new UnitRequest();
        mockUnitRequest.setId(1);

        mockCurrencyRequest = new CurrencyRequest();
        mockCurrencyRequest.setId(1);

        mockSequenceRequest = new SequenceRequest();
        mockSequenceRequest.setRecId(1);
        mockSequenceRequest.setSequence(1);
    }

    @Test
    public void getTenantUnit_Success() {
        when(tenantUnitService.getByUnitId(1)).thenReturn(mockTenantUnitDto);
        ResponseEntity response = tenantUnitController.getTenantUnit(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getTenantUnit_NotFound() {
        when(tenantUnitService.getByUnitId(1)).thenReturn(null);
        ResponseEntity response = tenantUnitController.getTenantUnit(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void searchUnit_Success() {
        TenantUnitSearchDto searchDto = new TenantUnitSearchDto();
        List<TenantUnitDto> tenantUnitList = new ArrayList<>();
        tenantUnitList.add(mockTenantUnitDto);
        searchDto.setTenantUnitList(tenantUnitList);
        searchDto.setTotal(1L);
        searchDto.setTotalPage(1);

        when(tenantUnitService.searchTenantUnitByCondition(any(UnitSearchRequest.class), any(Pageable.class)))
                .thenReturn(searchDto);

        ResponseEntity response = tenantUnitController.searchUnit(mockSearchRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createTenantUnit_Success() {
        when(tenantUnitService.createTenantUnit(any(UnitRequest.class))).thenReturn(1);
        ResponseEntity response = tenantUnitController.createTenantUnit(mockUnitRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createTenantUnit_NotFound() {
        when(tenantUnitService.createTenantUnit(any(UnitRequest.class))).thenReturn(0);
        ResponseEntity response = tenantUnitController.createTenantUnit(mockUnitRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createTenantUnit_Conflict() {
        when(tenantUnitService.createTenantUnit(any(UnitRequest.class))).thenReturn(-1);
        ResponseEntity response = tenantUnitController.createTenantUnit(mockUnitRequest);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void updateTenantUnit_Success() {
        when(tenantUnitService.updateTenantUnit(any(UnitRequest.class))).thenReturn(1);
        ResponseEntity response = tenantUnitController.updateTenantUnit(mockUnitRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void updateTenantUnit_NotFound() {
        when(tenantUnitService.updateTenantUnit(any(UnitRequest.class))).thenReturn(0);
        ResponseEntity response = tenantUnitController.updateTenantUnit(mockUnitRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateTenantUnitSequence_Success() {
        when(tenantUnitService.updateTenantUnitSequence(any(SequenceRequest.class))).thenReturn(mockTenantUnitDto);
        ResponseEntity response = tenantUnitController.updateTenantUnitSequence(mockSequenceRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateTenantUnitSequence_Error() {
        when(tenantUnitService.updateTenantUnitSequence(any(SequenceRequest.class))).thenReturn(null);
        ResponseEntity response = tenantUnitController.updateTenantUnitSequence(mockSequenceRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
