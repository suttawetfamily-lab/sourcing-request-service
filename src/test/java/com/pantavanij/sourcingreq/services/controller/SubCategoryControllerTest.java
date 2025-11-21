package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.SubCategoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.request.SubCategoryRequest;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SubCategoryService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubCategoryControllerTest {

    @Mock
    private SubCategoryService subCategoryService;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private SubCategoryController subCategoryController;

    private Tenant mockTenant;
    private SubCategoryDto mockSubCategoryDto;
    private List<OptionDto> mockOptionDtos;

    @Before
    public void setup() {
        mockTenant = new Tenant();
        mockTenant.setRecId(1);
        mockTenant.setCode("TEST_TENANT");

        mockSubCategoryDto = new SubCategoryDto();
        mockSubCategoryDto.setRecId(1);
        mockSubCategoryDto.setName("Test SubCategory");

        mockOptionDtos = List.of(
                new OptionDto("1", "Option 1", "Option 1", false),
                new OptionDto("2", "Option 2", "Option 2", false)
        );

        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
        }
    }

    @Test
    public void getSubCategoryBySearchTerm_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(subCategoryService.getSubCategoryByTenantIdAndSearchTermAndCategoryId(anyInt(), anyString(), anyInt()))
                    .thenReturn(mockOptionDtos);

            ResponseEntity<?> response = subCategoryController.getSubCategoryBySearchTerm("test", 1);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test(expected = BusinessException.class)
    public void getSubCategoryBySearchTerm_TenantNotFound() {
        subCategoryController.getSubCategoryBySearchTerm("test", 1);
    }

    @Test
    public void viewSubCategory_Success() {
        when(subCategoryService.getSubCategoryById(anyInt())).thenReturn(mockSubCategoryDto);

        ResponseEntity response = subCategoryController.viewSubCategory(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void viewSubCategory_NotFound() {
        when(subCategoryService.getSubCategoryById(anyInt())).thenReturn(null);

        ResponseEntity response = subCategoryController.viewSubCategory(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void submitSubCategory_Success() {
        when(subCategoryService.createSubCategory(any(SubCategoryRequest.class))).thenReturn(mockSubCategoryDto);

        SubCategoryRequest request = new SubCategoryRequest();
        ResponseEntity response = subCategoryController.submitSubCategory(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void updateSubCategory_Success() {
        when(subCategoryService.updateSubCategory(any(SubCategoryRequest.class))).thenReturn(mockSubCategoryDto);

        SubCategoryRequest request = new SubCategoryRequest();
        ResponseEntity response = subCategoryController.updateSubCategory(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void updateSubCategorySequence_Success() {
        when(subCategoryService.updateSubCategorySequence(any(SequenceRequest.class))).thenReturn(mockSubCategoryDto);

        SequenceRequest request = new SequenceRequest();
        ResponseEntity response = subCategoryController.updateSubCategorySequence(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void deleteSubCategory_Success() {
        when(subCategoryService.deleteSubCategoryByRecId(anyInt(), false)).thenReturn(1);

        ResponseEntity response = subCategoryController.deleteSubCategory(1, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteSubCategory_Referenced() {
        when(subCategoryService.deleteSubCategoryByRecId(anyInt(), false)).thenReturn(-1);

        ResponseEntity response = subCategoryController.deleteSubCategory(1, false);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void deleteSubCategory_NotFound() {
        when(subCategoryService.deleteSubCategoryByRecId(anyInt(), false)).thenReturn(0);

        ResponseEntity response = subCategoryController.deleteSubCategory(1, false);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
