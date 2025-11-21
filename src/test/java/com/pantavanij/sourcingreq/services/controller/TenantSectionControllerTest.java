package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.TenantSectionRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantSectionSearchRequest;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.PathUrl;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSectionService;
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
public class TenantSectionControllerTest {

    @Mock
    private TenantSectionService tenantSectionService;

    @InjectMocks
    private TenantSectionController tenantSectionController;

    private List<TenantSectionDto> tenantSectionDtoList;
    private TenantSectionDto tenantSectionDto;

    @Before
    public void setup() {
        tenantSectionDtoList = new ArrayList<>();
        tenantSectionDto = new TenantSectionDto();
        tenantSectionDto.setId(1L);
        tenantSectionDto.setSectionName("Test Section");
        tenantSectionDtoList.add(tenantSectionDto);
    }

    @Test
    public void getRequestFields_Success() {
        when(tenantSectionService.getRequestFields(any(),0)).thenReturn(tenantSectionDtoList);

        ResponseEntity response = tenantSectionController.getRequestFields(PathUrl.SOURCING_REQUEST, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void getRequestFields_NotFound() {
        when(tenantSectionService.getRequestFields(any(),0)).thenReturn(new ArrayList<>());

        ResponseEntity response = tenantSectionController.getRequestFields(PathUrl.SOURCING_REQUEST, 0);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createNewTenantSection_Success() {
        TenantSectionRequest request = new TenantSectionRequest();
        when(tenantSectionService.createNewTenantSection(any(), 0)).thenReturn(tenantSectionDto);

        ResponseEntity response = tenantSectionController.createNewTenantSection(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void updateTenantSection_Success() {
        TenantSectionRequest request = new TenantSectionRequest();
        when(tenantSectionService.updateTenantSection(any(), 0)).thenReturn(tenantSectionDto);

        ResponseEntity response = tenantSectionController.updateTenantSection(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteTenantSection_Success() {
        when(tenantSectionService.deleteTenantSection(any(),0)).thenReturn(true);

        ResponseEntity response = tenantSectionController.deleteTenantSection(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void tenantSectionSearch_Success() {
        TenantSectionSearchRequest request = new TenantSectionSearchRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setSortBy("name");
        request.setSortOrder("desc");

        TenantSectionSearchDto searchDto = TenantSectionSearchDto.builder()
                .tenantSectionList(tenantSectionDtoList)
                .pageSize(10)
                .totalPage(1)
                .total(1L)
                .build();

        when(tenantSectionService.searchTenantSectionByCondition(any(), any())).thenReturn(searchDto);

        ResponseEntity response = tenantSectionController.tenantSectionSearch(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getTenantSectionView_Success() {
        when(tenantSectionService.getByRecId(any())).thenReturn(tenantSectionDto);

        ResponseEntity response = tenantSectionController.getTenantSectionView(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getRequestItemFields_Success() {
        when(tenantSectionService.getRequestItemFields(any(),0)).thenReturn(tenantSectionDtoList);

        ResponseEntity response = tenantSectionController.getRequestItemFields(1,0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getRequestReportFields_Success() {
        when(tenantSectionService.getRequestReportFields(0)).thenReturn(tenantSectionDtoList);

        ResponseEntity response = tenantSectionController.getRequestReportFields(0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
