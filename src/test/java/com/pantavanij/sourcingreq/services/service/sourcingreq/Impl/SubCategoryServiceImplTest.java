package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.github.javafaker.*;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.SubCategoryRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubCategoryServiceImplTest {

    @Mock
    private ExistingPriceItemSubCategoryRepository existingPriceItemSubCategoryRepository;

    @Mock
    private PurchaserRepository purchaserRepository;

    @Mock
    private RequestSubCategoryRepository requestSubCategoryRepository;

    @Mock
    private SubCategoryPurchaserRepository subCategoryPurchaserRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private UaaService uaaService;

    @Mock
    private UserDto mockUser;

    @InjectMocks
    private SubCategoryServiceImpl subCategoryService;

    private Tenant mockTenant;
    private SubCategory mockSubCategory;
    private SubCategoryRequest mockRequest;

    @Before
    public void setUp() {
        mockTenant = Tenant.builder()
                .recId(1)
                .code("TEST")
                .build();

        mockSubCategory = SubCategory.builder()
                .recId(1)
                .tenant(mockTenant)
                .category(Category.builder().recId(1).build())
                .code("TEST-CODE")
                .name("Test SubCategory")
                .sequence(1)
                .isDefault(false)
                .active(true)
                .build();

        mockRequest = new SubCategoryRequest();
        mockRequest.setRecId(1);
        mockRequest.setCategoryId(1);
        mockRequest.setCode("TEST-CODE");
        mockRequest.setName("Test SubCategory");
        mockRequest.setSequence(1);
        mockRequest.setDefault(false);
        mockRequest.setActive(true);

        mockUser = new UserDto();
        mockUser.setTenantId("TEST-TENANT-ID");
        mockUser.setUsername("testuser");
        mockUser.setFullName("testuser");
        mockUser.setEmail("testuser@example.com");
    }

    @Test
    public void getSubCategoryByTenantIdAndSearchTermAndCategoryId_Success() {
        when(subCategoryRepository.findByTenantIdAndCategoryIdAndCodeOrName(anyInt(), anyInt(), anyString()))
                .thenReturn(List.of(mockSubCategory));

        List<OptionDto> result = subCategoryService.getSubCategoryByTenantIdAndSearchTermAndCategoryId(1, "test", 1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void createSubCategory_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST");
            appUtilMock.when(AppUtil::getUserName).thenReturn("testUser");

            // Setup tenant with required data
            Tenant tenant = Tenant.builder()
                    .recId(1)
                    .code("TEST")
                    .build();

            // Setup request with required data
            SubCategoryRequest request = new SubCategoryRequest();
            request.setRecId(1);
            request.setCategoryId(1);
            request.setSequence(0);

            // Mock repository calls
            when(tenantService.findByCode("TEST")).thenReturn(tenant);
            when(subCategoryRepository.findFirstByTenantRecIdAndCategoryRecIdOrderBySequenceDesc(1, 1))
                    .thenReturn(Optional.of(mockSubCategory));
            when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(mockSubCategory);

            SubCategoryDto result = subCategoryService.createSubCategory(request);

            assertNotNull(result);
            assertEquals(mockSubCategory.getRecId(), result.getRecId());

            // Verify interactions
            verify(tenantService).findByCode("TEST");
            verify(subCategoryRepository).save(any(SubCategory.class));
        }
    }

    @Test
    public void deleteSubCategoryByRecId_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST");
            appUtilMock.when(AppUtil::getUserName).thenReturn("testUser");

            // Setup test data
            Tenant tenant = Tenant.builder()
                    .recId(1)
                    .code("TEST")
                    .name("Test Tenant")
                    .build();

            Category category = Category.builder()
                    .recId(1)
                    .build();

            SubCategory subCategory = SubCategory.builder()
                    .recId(1)
                    .tenant(tenant)
                    .category(category)
                    .code("TEST-CODE")
                    .name("Test SubCategory")
                    .sequence(1)
                    .build();

            // Setup mocks
            when(tenantService.findByCode("TEST")).thenReturn(tenant);
            when(subCategoryRepository.findSubCategoryByRecId(1)).thenReturn(subCategory);
            when(existingPriceItemSubCategoryRepository.findBySubCategory_RecIdIn(anyList())).thenReturn(List.of());
            when(requestSubCategoryRepository.findBySubCategory_RecIdIn(anyList())).thenReturn(List.of());
            when(subCategoryPurchaserRepository.findBySubCategory_RecIdIn(anyList())).thenReturn(List.of());

            int result = subCategoryService.deleteSubCategoryByRecId(1, false);

            // Verify results
            assertEquals(1, result);

            // Verify interactions
            verify(tenantService).findByCode("TEST");
            verify(subCategoryRepository).findSubCategoryByRecId(1);
            verify(subCategoryRepository).delete(any(SubCategory.class));
        }
    }

    @Test
    public void getSubCategoryById_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Mock static methods
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST");
            UserDto mockUserDto = new UserDto();
            mockUserDto.setUsername("testUser");
            appUtilMock.when(AppUtil::getUser).thenReturn(mockUserDto);

            // Setup test data
            Tenant tenant = Tenant.builder()
                    .recId(1)
                    .code("TEST")
                    .name("Test Tenant")
                    .build();

            SubCategory subCategory = SubCategory.builder()
                    .recId(1)
                    .tenant(tenant)
                    .code("TEST-CODE")
                    .name("Test SubCategory")
                    .sequence(1)
                    .purchaserList(List.of())
                    .build();

            // Setup mocks with proper matchers
            when(tenantService.findByCode("TEST")).thenReturn(tenant);
            when(subCategoryRepository.findByRecIdAndTenant(eq(1), any(Tenant.class)))
                    .thenReturn(Optional.of(subCategory));
            when(uaaService.getUserTimeZone(any(UserDto.class), any())).thenReturn("UTC");

            SubCategoryDto result = subCategoryService.getSubCategoryById(1);

            // Verify results
            assertNotNull(result);
            assertEquals(subCategory.getRecId(), result.getRecId());

            // Verify interactions
            verify(tenantService).findByCode("TEST");
            verify(subCategoryRepository).findByRecIdAndTenant(eq(1), any(Tenant.class));
            verify(uaaService).getUserTimeZone(any(UserDto.class), any());
        }
    }

    @Test
    public void updateSubCategory_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Mock static methods
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST");
            appUtilMock.when(AppUtil::getUserName).thenReturn("testUser");

            // Setup complete test data
            Tenant tenant = Tenant.builder()
                    .recId(1)
                    .code("TEST")
                    .name("Test Tenant")
                    .build();

            Purchaser purchaser = Purchaser.builder()
                    .recId(1)
                    .purchaserName("Test Purchaser")
                    .build();

            SubCategoryRequest request = new SubCategoryRequest();
            request.setRecId(1);
            request.setCategoryId(1);
            request.setCode("TEST-CODE");
            request.setName("Test SubCategory");
            request.setSequence(1);
            request.setPurchasersId(List.of(1, 2));

            // Setup all required mocks
            when(tenantService.findByCode("TEST")).thenReturn(tenant);
            when(subCategoryRepository.findByRecId(1)).thenReturn(Optional.of(mockSubCategory));
            when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(mockSubCategory);
            when(purchaserRepository.findFirstByRecId(anyInt())).thenReturn(Optional.of(purchaser));

            SubCategoryDto result = subCategoryService.updateSubCategory(request);

            // Verify results
            assertNotNull(result);
            assertEquals(mockSubCategory.getRecId(), result.getRecId());

            // Verify all interactions
            verify(tenantService).findByCode("TEST");
            verify(subCategoryRepository).save(any(SubCategory.class));
            verify(purchaserRepository, times(request.getPurchasersId().size())).findFirstByRecId(anyInt());
        }
    }

    @Test
    public void getSubCategoryByTenantId_Success() {
        when(subCategoryRepository.findByTenantId(anyInt()))
                .thenReturn(List.of(mockSubCategory));

        List<SubCategoryDto> result = subCategoryService.getSubCategoryByTenantId(1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void getSubCategoryOptionDtoByTenantId_Success() {
        when(subCategoryRepository.findByTenantId(anyInt()))
                .thenReturn(List.of(mockSubCategory));

        List<OptionDto> result = subCategoryService.getSubCategoryOptionDtoByTenantId(1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
