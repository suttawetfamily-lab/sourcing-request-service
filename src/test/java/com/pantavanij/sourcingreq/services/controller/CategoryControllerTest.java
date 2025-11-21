package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategorySearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.CategoryRequest;
import com.pantavanij.sourcingreq.services.domain.request.CategorySearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.PurchaserRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private PurchaserRepository purchaserRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private Tenant mockTenant;

    @Mock
    private List<OptionDto> mockCategoryOptions;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryDto mockCategoryDto;
    private CategoryRequest mockCategoryRequest;

    @Before
    public void setup() {
        mockCategoryDto = new CategoryDto();
        mockCategoryDto.setRecId(1);
        mockCategoryDto.setName("Test Category");

        mockCategoryRequest = new CategoryRequest();
        mockCategoryRequest.setName("Test Category");
        mockCategoryRequest.setPurchasersId(Arrays.asList(1, 2));

        mockTenant = new Tenant();
        mockTenant.setRecId(1);
        mockTenant.setCode("TEST_TENANT");
        mockTenant.setName("TEST_TENANT");
        mockTenant.setDescription("TEST_TENANT");
    }

    @Test
    public void getCategoryBySearchTermShouldReturnMatchingOptions() {
        String searchTerm = "test";

        try (MockedStatic<AppUtil> appUtilMock = Mockito.mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(categoryService.getCategoryByTenantIdAndSearchTerm(mockTenant.getRecId(), searchTerm, 2))
                .thenReturn(mockCategoryOptions);

            ResponseEntity<ApiResponse<List<OptionDto>>> response =
                categoryController.getCategoryBySearchTerm(searchTerm, 2);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(mockCategoryOptions, response.getBody().getData());
        }
    }

    @Test
    public void getCategoryBySearchTermShouldThrowExceptionWhenTenantNotFound() {
        String searchTerm = "test";

        try (MockedStatic<AppUtil> appUtilMock = Mockito.mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            when(tenantService.findByCode("TEST_TENANT")).thenReturn(null);

            try {
                categoryController.getCategoryBySearchTerm(searchTerm, 2);
            } catch (BusinessException e) {
                assertEquals(ApiMessage.E7016, e.getApiMessage());
                assertEquals(ApiMessage.E7016.description(), e.getDescription());
            }
        }
    }

    @Test
    public void getCategoryBySearchTermShouldReturnEmptyListWhenNoMatches() {
        String searchTerm = "nonexistent";

        try (MockedStatic<AppUtil> appUtilMock = Mockito.mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(categoryService.getCategoryByTenantIdAndSearchTerm(mockTenant.getRecId(), searchTerm, 2))
                .thenReturn(new ArrayList<>());

            ResponseEntity<ApiResponse<List<OptionDto>>> response =
                categoryController.getCategoryBySearchTerm(searchTerm, 2);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().getData().isEmpty());
        }
    }
    @Test
    public void getCategoryViewShouldReturnCategoryDto() {
        when(categoryService.findCategoryByRecId(1)).thenReturn(mockCategoryDto);
        ResponseEntity response = categoryController.getCategoryView(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void searchCategoryShouldReturnMatchingResults() {
        CategorySearchRequest searchRequest = new CategorySearchRequest();
        searchRequest.setPage(1);
        searchRequest.setPageSize(10);
        searchRequest.setSortBy("name");
        searchRequest.setSortOrder("desc");

        CategorySearchDto searchDto = new CategorySearchDto();
        searchDto.setCategoryDtoList(Arrays.asList(mockCategoryDto));
        searchDto.setTotal(1L);
        searchDto.setTotalPage(1);
        searchDto.setPageSize(10);

        when(categoryService.searchCategoryByCondition(any(), any())).thenReturn(searchDto);

        ResponseEntity response = categoryController.searchCategory(searchRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createCategoryShouldReturnNewCategory() {
        when(categoryService.saveCategory(any(CategoryRequest.class))).thenReturn(mockCategoryDto);
        ResponseEntity response = categoryController.createCategory(mockCategoryRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void updateCategoryShouldReturnUpdatedCategory() {
        Purchaser purchaser1 = new Purchaser();
        purchaser1.setRecId(1);
        Purchaser purchaser2 = new Purchaser();
        purchaser2.setRecId(2);

        when(purchaserRepository.findFirstByRecId(1)).thenReturn(Optional.of(purchaser1));
        when(purchaserRepository.findFirstByRecId(2)).thenReturn(Optional.of(purchaser2));
        when(categoryService.updateCategory(any(CategoryRequest.class), any())).thenReturn(mockCategoryDto);

        ResponseEntity response = categoryController.updateCategory(mockCategoryRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateCategorySequenceShouldReturnUpdatedSequence() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        sequenceRequest.setRecId(1);
        sequenceRequest.setSequence(1);

        when(categoryService.updateCategorySequence(any(SequenceRequest.class))).thenReturn(mockCategoryDto);

        ResponseEntity response = categoryController.updateCategorySequence(sequenceRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteCategoryShouldRemoveCategory() {
        when(categoryService.deleteCategory(1)).thenReturn(1);
        ResponseEntity response = categoryController.deleteCategory(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteCategoryShouldReturnConflictWhenReferenced() {
        when(categoryService.deleteCategory(1)).thenReturn(-1);
        ResponseEntity response = categoryController.deleteCategory(1);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}