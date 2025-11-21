package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TypeService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
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

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TypeControllerTest {

    @Mock
    private TypeService typeService;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private TypeController typeController;

    private Tenant mockTenant;
    private List<OptionDto> mockOptions;
    private TypeDto mockTypeDto;

    @Before
    public void setUp() {
        mockTenant = new Tenant();
        mockTenant.setRecId(1);

        mockOptions = Arrays.asList(
            new OptionDto("1", "1", "Type1", true),
            new OptionDto("2", "2", "Type2", true)
        );

        mockTypeDto = new TypeDto();
        mockTypeDto.setRecId(1);
        mockTypeDto.setCode("TEST");
    }

    @Test
    public void getType_WithoutReportFilter_ShouldReturnTypeList() {
        try (MockedStatic<AppUtil> mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn("TEST");
            when(tenantService.findByCode("TEST")).thenReturn(mockTenant);
            when(typeService.getTypeByTenantId(mockTenant.getRecId())).thenReturn(mockOptions);

            ResponseEntity<?> response = typeController.getType(false);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test
    public void getType_WithReportFilter_ShouldReturnTypeListWithAllType() {
        try (MockedStatic<AppUtil> mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn("TEST");
            when(tenantService.findByCode("TEST")).thenReturn(mockTenant);
            when(typeService.getTypeByTenantId(mockTenant.getRecId())).thenReturn(new ArrayList<>(mockOptions));

            ResponseEntity<?> response = typeController.getType(true);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test
    public void viewType_WhenTypeExists_ShouldReturnType() {
        when(typeService.getByTypeId(1)).thenReturn(mockTypeDto);

        ResponseEntity<?> response = typeController.viewType(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void searchType_WithValidRequest_ShouldReturnSearchResults() {
        TypeSearchRequest request = new TypeSearchRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setSortBy("code");
        request.setSortOrder("desc");

        TypeSearchDto searchDto = new TypeSearchDto();
        searchDto.setTotal(2L);
        searchDto.setTotalPage(1);
        searchDto.setTypeList(Arrays.asList(mockTypeDto));

        when(typeService.searchTypeListByCondition(any(), any(PageRequest.class))).thenReturn(searchDto);

        ResponseEntity<?> response = typeController.searchType(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void createType_WithValidRequest_ShouldReturnCreatedType() {
        TypeRequest request = new TypeRequest();
        when(typeService.createType(request)).thenReturn(1);

        ResponseEntity<?> response = typeController.createType(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void updateType_WithValidRequest_ShouldReturnUpdatedType() {
        TypeRequest request = new TypeRequest();
        request.setId(1);
        when(typeService.updateType(request)).thenReturn(1);

        ResponseEntity<?> response = typeController.updateType(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void updateTypeSequence_WithValidRequest_ShouldReturnUpdatedType() {
        SequenceRequest request = new SequenceRequest();
        when(typeService.updateTypeSequence(request)).thenReturn(mockTypeDto);

        ResponseEntity<?> response = typeController.updateTenantCurrencySequence(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
