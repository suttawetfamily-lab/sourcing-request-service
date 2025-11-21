package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.UnitMasterDataRequest;
import com.pantavanij.sourcingreq.services.domain.request.UnitSearchRequest;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UnitService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UnitControllerTest {

    @InjectMocks
    private UnitController unitController;

    @Mock
    private UnitService unitService;

    @Mock
    private TenantService tenantService;

    @Before
    public void setup() {

    }

    @Test
    public void getUnitByTenantIdV1_Success() {
        List<UnitDto> mockUnits = new ArrayList<>();
        mockUnits.add(new UnitDto());

        when(unitService.getUnitByTenantIdV1(anyInt(), anyInt())).thenReturn(mockUnits);

        ResponseEntity<?> response = unitController.getUnitByTenantIdV1(1, 2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(unitService).getUnitByTenantIdV1(1, 2);
    }

    @Test
    public void getUnitByTenantIdV1_EmptyList() {
        when(unitService.getUnitByTenantIdV1(anyInt(), anyInt())).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = unitController.getUnitByTenantIdV1(1, 2);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getUnitBySearchTerm_Success() {
        Tenant mockTenant = new Tenant();
        mockTenant.setRecId(1);
        List<OptionDto> mockOptions = new ArrayList<>();

        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST");
            when(tenantService.findByCode(anyString())).thenReturn(mockTenant);
            when(unitService.getUnitSearchTerm(anyInt(), anyString(), anyInt())).thenReturn(mockOptions);

            ResponseEntity<?> response = unitController.getUnitBySearchTerm("test", 2);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void createUnit_Success() {
        UnitMasterDataRequest request = new UnitMasterDataRequest();
        request.setRecId(1);
        request.setCode("TEST");
        request.setName("Test Unit");
        when(unitService.createUnit(request)).thenReturn(1);

        ResponseEntity<?> response = unitController.createUnit(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createUnit_Conflict() {
        UnitMasterDataRequest request = new UnitMasterDataRequest();
        when(unitService.createUnit(request)).thenReturn(-1);

        ResponseEntity<?> response = unitController.createUnit(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void searchUnit_Success() {
        UnitSearchRequest request = new UnitSearchRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setSortBy("code");
        request.setSortOrder("desc");

        UnitSearchDto mockSearchDto = new UnitSearchDto();
        mockSearchDto.setUnitList(new ArrayList<>());
        mockSearchDto.getUnitList().add(new UnitMasterDataDto());

        when(unitService.searchUnitsByCondition(any(), any())).thenReturn(mockSearchDto);

        ResponseEntity<?> response = unitController.searchUnit(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteUnit_Success() {
        when(unitService.deleteUnitById(anyInt())).thenReturn(1);

        ResponseEntity<?> response = unitController.deleteUnit(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteUnit_NotFound() {
        when(unitService.deleteUnitById(anyInt())).thenReturn(0);

        ResponseEntity<?> response = unitController.deleteUnit(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateUnit_Success() {
        UnitMasterDataRequest request = new UnitMasterDataRequest();
        when(unitService.updateUnit(any())).thenReturn(1);

        ResponseEntity<?> response = unitController.updateUnit(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateUnit_NotFound() {
        UnitMasterDataRequest request = new UnitMasterDataRequest();
        when(unitService.updateUnit(any())).thenReturn(0);

        ResponseEntity<?> response = unitController.updateUnit(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAllUnit_Success() {
        List<OptionDto> mockUnits = new ArrayList<>();
        mockUnits.add(new OptionDto());

        when(unitService.getAllUnit()).thenReturn(mockUnits);

        ResponseEntity<?> response = unitController.getAllUnit();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
