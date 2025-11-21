package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailFieldNameDto;
import com.pantavanij.sourcingreq.services.domain.request.TenantSectionDetailRequest;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSectionDetailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TenantSectionDetailControllerTest {

    @Mock
    private TenantSectionDetailService tenantSectionDetailService;

    @InjectMocks
    private TenantSectionDetailController tenantSectionDetailController;

    private TenantSectionDetailRequest request;
    private TenantSectionDetailDto detailDto;
    private List<TenantSectionDetailFieldNameDto> fieldNameDtoList;

    @Before
    public void setup() {
        request = new TenantSectionDetailRequest();
        detailDto = new TenantSectionDetailDto();
        fieldNameDtoList = List.of(new TenantSectionDetailFieldNameDto());
    }

    @Test
    public void createTenantSectionDetail_Success() {
        when(tenantSectionDetailService.createTenantSectionDetail(any())).thenReturn(detailDto);

        ResponseEntity response = tenantSectionDetailController.createTenantSectionDetail(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createTenantSectionDetail_Failure() {
        when(tenantSectionDetailService.createTenantSectionDetail(any())).thenReturn(null);

        ResponseEntity response = tenantSectionDetailController.createTenantSectionDetail(request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void updateTenantSectionDetail_Success() {
        when(tenantSectionDetailService.updateTenantSectionDetail(any())).thenReturn(detailDto);

        ResponseEntity response = tenantSectionDetailController.updateTenantSectionDetail(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateTenantSectionDetail_Failure() {
        when(tenantSectionDetailService.updateTenantSectionDetail(any())).thenReturn(null);

        ResponseEntity response = tenantSectionDetailController.updateTenantSectionDetail(request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getTenantSectionDetailFieldName_Success() {
        when(tenantSectionDetailService.getTenantSectionDetailFieldName(any(),0)).thenReturn(fieldNameDtoList);

        ResponseEntity response = tenantSectionDetailController.getTenantSectionDetailFieldName(1, "sectionType", 0);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getTenantSectionDetailFieldName_Failure() {
        when(tenantSectionDetailService.getTenantSectionDetailFieldName(any(), 0)).thenReturn(null);

        ResponseEntity response = tenantSectionDetailController.getTenantSectionDetailFieldName(1, "sectionType", 0);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
