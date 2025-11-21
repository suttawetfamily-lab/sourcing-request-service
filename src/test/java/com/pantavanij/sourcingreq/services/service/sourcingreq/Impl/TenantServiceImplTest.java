package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.TenantDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.TenantRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantSearchRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRepository;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenantServiceImplTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantServiceImpl tenantService;

    private Tenant mockTenant;
    private TenantRequest mockTenantRequest;
    private final String TIMEZONE = "Asia/Bangkok";

    @Before
    public void setUp() {
        mockTenant = Tenant.builder()
                .recId(1)
                .code("TEST001")
                .name("Test Tenant")
                .description("Test Description")
                .createdBy("admin")
                .build();

        mockTenantRequest = new TenantRequest();
        mockTenantRequest.setRecId(1);
        mockTenantRequest.setCode("TEST001");
        mockTenantRequest.setName("Test Tenant");
        mockTenantRequest.setDescription("Test Description");
    }

    @Test
    public void findByCode_ShouldReturnTenant() {
        when(tenantRepository.findTenantByCode("TEST001")).thenReturn(mockTenant);

        Tenant result = tenantService.findByCode("TEST001");

        assertNotNull(result);
        assertEquals("TEST001", result.getCode());
        verify(tenantRepository, times(1)).findTenantByCode("TEST001");
    }

    @Test
    public void getTenantDtoByRecId_ShouldReturnTenantDto() {
        when(tenantRepository.findTenantByRecId(1)).thenReturn(mockTenant);

        TenantDto result = tenantService.getTenantDtoByRecId(1, TIMEZONE);

        assertNotNull(result);
        assertEquals(mockTenant.getCode(), result.getTenantCode());
        verify(tenantRepository, times(1)).findTenantByRecId(1);
    }

    @Test
    public void searchTenantListByCondition_ShouldReturnTenantSearchDto() {
        List<Tenant> tenantList = new ArrayList<>();
        tenantList.add(mockTenant);
        Page<Tenant> tenantPage = new PageImpl<>(tenantList);
        Pageable pageable = PageRequest.of(0, 10);
        TenantSearchRequest searchRequest = new TenantSearchRequest();

        when(tenantRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(tenantPage);

        TenantSearchDto result = tenantService.searchTenantListByCondition(searchRequest, pageable, TIMEZONE);

        assertNotNull(result);
        assertEquals(1, result.getTenantList().size());
        assertEquals(1, result.getTotal());
        verify(tenantRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void createTenant_ShouldReturnRecId() {
        when(tenantRepository.save(any(Tenant.class))).thenReturn(mockTenant);

        Integer result = tenantService.createTenant(mockTenantRequest);

        assertNotNull(result);
        assertEquals(mockTenant.getRecId(), result);
        verify(tenantRepository, times(1)).save(any(Tenant.class));
    }

    @Test
    public void updateTenant_ShouldReturnRecId() {
        when(tenantRepository.findTenantByRecId(1)).thenReturn(mockTenant);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(mockTenant);

        Integer result = tenantService.updateTenant(mockTenantRequest);

        assertNotNull(result);
        assertEquals(mockTenant.getRecId(), result);
        verify(tenantRepository, times(1)).findTenantByRecId(1);
        verify(tenantRepository, times(1)).save(any(Tenant.class));
    }

    @Test
    public void updateTenant_WhenTenantNotFound_ShouldReturnZero() {
        when(tenantRepository.findTenantByRecId(1)).thenReturn(null);

        Integer result = tenantService.updateTenant(mockTenantRequest);

        assertEquals(Integer.valueOf(0), result);
        verify(tenantRepository, times(1)).findTenantByRecId(1);
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    @Test(expected = RuntimeException.class)
    public void createTenant_WhenExceptionOccurs_ShouldThrowRuntimeException() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class);
             MockedStatic<DateTimeUtil> dateTimeUtilMock = mockStatic(DateTimeUtil.class)) {

            appUtilMock.when(AppUtil::getUserName).thenReturn("testUser");
            dateTimeUtilMock.when(DateTimeUtil::getTimestampUTC).thenReturn(DateTimeUtil.getTimestampUTC());
            when(tenantRepository.save(any(Tenant.class))).thenThrow(new RuntimeException());

            tenantService.createTenant(mockTenantRequest);
        }
    }
}
