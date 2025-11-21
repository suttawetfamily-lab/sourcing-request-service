package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.category.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Category;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.CategoryRequest;
import com.pantavanij.sourcingreq.services.domain.request.CategorySearchRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryPurchaserRepository categoryPurchaserRepository;
    @Mock
    private ExistingPriceItemCategoryRepository existingPriceItemCategoryRepository;
    @Mock
    private RequestCategoryRepository requestCategoryRepository;
    @Mock
    private SubCategoryRepository subCategoryRepository;
    @Mock
    private TenantService tenantService;
    @Mock
    private UaaService uaaService;

    private Category testCategory;
    private Tenant testTenant;

    @Before
    public void setUp() {
        testTenant = new Tenant();
        testTenant.setRecId(1);
        testTenant.setCode("TEST_TENANT");

        testCategory = Category.builder()
                .recId(1)
                .tenant(testTenant)
                .code("TEST_CAT")
                .name("Test Category")
                .sequence(1)
                .isDefault(true)
                .active(true)
                .build();
    }

    @Test
    public void getCategoryByTenantIdAndSearchTerm_Success() {
        String searchTerm = "test";
        List<Category> categories = new ArrayList<>();
        categories.add(testCategory);

        when(categoryRepository.findByTenantIdAndCodeOrName(anyInt(), anyString(), anyInt()))
                .thenReturn(categories);

        assertNotNull(categoryService.getCategoryByTenantIdAndSearchTerm(1, searchTerm, 2));
    }
    @Test
    public void searchCategoryByCondition_Success() {
        // Set up static mock for UserDetailServiceUtil
        try (MockedStatic<UserDetailServiceUtil> userDetailServiceUtil = mockStatic(UserDetailServiceUtil.class)) {
            // Initialize test data
            CategorySearchRequest searchRequest = new CategorySearchRequest();
            Pageable pageable = PageRequest.of(0, 10);

            Category testCategory = Category.builder()
                    .recId(1)
                    .tenant(testTenant)
                    .code("TEST_CAT")
                    .name("Test Category")
                    .sequence(1)
                    .isDefault(true)
                    .active(true)
                    .purchaserList(new ArrayList<>())
                    .subCategoryList(new ArrayList<>())
                    .createdBy("test_user")
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .build();

            List<Category> categories = new ArrayList<>();
            categories.add(testCategory);
            Page<Category> categoryPage = new PageImpl<>(categories);

            // Mock all required dependencies
            when(categoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(categoryPage);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");
            userDetailServiceUtil.when(() -> UserDetailServiceUtil.getFullName(anyString())).thenReturn("Test User");

            // Execute test
            CategorySearchDto result = categoryService.searchCategoryByCondition(searchRequest, pageable);

            // Verify results
            assertNotNull(result);
            assertEquals(1, result.getPageSize());
        }
    }
    @Test
    public void saveCategory_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setCode("TEST_CAT");
        request.setName("Test Category");
        request.setSequence(1);
        request.setPurchasersId(new ArrayList<>());
        request.setSubCategoryRequestList(new ArrayList<>());

        when(tenantService.findByCode(any())).thenReturn(testTenant);
        when(categoryRepository.findFirstByTenantRecIdOrderBySequenceDesc(anyInt()))
                .thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryDto result = categoryService.saveCategory(request);

        assertNotNull(result);
        assertEquals(testCategory.getCode(), result.getCode());
    }
    @Test
    public void deleteCategory_Success() {
        when(tenantService.findByCode(any())).thenReturn(testTenant);
        when(categoryRepository.findFirstByRecId(anyInt())).thenReturn(Optional.of(testCategory));
        when(categoryPurchaserRepository.findByCategory_RecIdIn(any())).thenReturn(new ArrayList<>());
        when(requestCategoryRepository.findByCategory_RecIdIn(any())).thenReturn(new ArrayList<>());
        when(existingPriceItemCategoryRepository.findByCategory_RecIdIn(any())).thenReturn(new ArrayList<>());
        when(categoryRepository.deleteByRecId(anyInt())).thenReturn(1);
        doNothing().when(subCategoryRepository).deleteAllByTenantRecIdAndCategoryRecId(anyInt(), anyInt());
        when(categoryRepository.findByTenantId(anyInt())).thenReturn(new ArrayList<>());

        int result = categoryService.deleteCategory(1);

        verify(subCategoryRepository).deleteAllByTenantRecIdAndCategoryRecId(anyInt(), anyInt());
        assertEquals(1, result);
    }
    @Test
    public void findCategoryByRecId_Success() {
        Category testCategory = Category.builder()
                .recId(1)
                .tenant(testTenant)
                .code("TEST_CAT")
                .name("Test Category")
                .sequence(1)
                .isDefault(true)
                .active(true)
                .purchaserList(new ArrayList<>())
                .subCategoryList(new ArrayList<>())
                .createdBy("test_user")
                .createdDate(DateTimeUtil.getTimestampUTC())
                .build();

        when(categoryRepository.findCategoryByRecId(anyInt())).thenReturn(testCategory);
        when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");

        CategoryDto result = categoryService.findCategoryByRecId(1);

        assertNotNull(result);
        assertEquals(testCategory.getRecId(), result.getRecId());
    }
}