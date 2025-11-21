package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSection;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetail;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenantSectionServiceImplTest {

    @InjectMocks
    private TenantSectionServiceImpl tenantSectionService;

    @Mock
    private TenantSectionRepository tenantSectionRepository;

    @Mock
    private TenantSectionDetailOptionRepository tenantSectionDetailOptionRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private UaaService uaaService;

    @Mock
    private TenantSectionDetailRepository tenantSectionDetailRepository;

    private Tenant mockTenant;
    private TenantSection mockTenantSection;
    private List<TenantSectionDetail> mockTenantSectionDetails;
    private UserDto mockUser;

    @Before
    public void setup() {
        mockTenant = new Tenant();
        mockTenant.setRecId(1);
        mockTenant.setCode("TEST_TENANT");

        mockTenantSection = new TenantSection();
        mockTenantSection.setId(1L);
        mockTenantSection.setTenant(mockTenant);
        mockTenantSection.setType("REQ");

        mockTenantSectionDetails = new ArrayList<>();
        TenantSectionDetail detail = new TenantSectionDetail();
        detail.setFieldName("testField");
        detail.setVisible(true);
        detail.setPreSpan(0);
        detail.setPostSpan(0);
        detail.setSpan(1);
        detail.setTenantSectionDetailValidatorList(new ArrayList<>());
        mockTenantSectionDetails.add(detail);

        mockTenantSection.setFields(mockTenantSectionDetails);

        mockUser = new UserDto();
        mockUser.setTenantId("TEST_TENANT");
        mockUser.setUsername("testUser");
        mockUser.setFullName("testUser");
    }

    @Test
    public void getRequestFields_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMock.when(AppUtil::getUserName).thenReturn("testUser");

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(tenantSectionRepository.getTenantSectionByTenantIdAndType(mockTenant.getRecId(), List.of("REQ")))
                    .thenReturn(List.of(mockTenantSection));

            List<TenantSectionDto> result = tenantSectionService.getRequestFields("TEST_PRIVILEGE",0);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());

            verify(tenantService, times(2)).findByCode("TEST_TENANT");
            verify(tenantSectionRepository).getTenantSectionByTenantIdAndType(mockTenant.getRecId(), List.of("REQ"));
        }
    }

    @Test
    public void getByRecId_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMock.when(AppUtil::getUser).thenReturn(mockUser);

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");
            when(tenantSectionRepository.findByRecId(1)).thenReturn(Optional.of(mockTenantSection));
            when(tenantSectionRepository.findByTenantAndTypeIn(any(), any())).thenReturn(List.of(mockTenantSection));
            when(tenantSectionDetailRepository.findByTenantAndTenantSectionIdIn(any(), any())).thenReturn(mockTenantSectionDetails);

            TenantSectionDto result = tenantSectionService.getByRecId(1);

            assertNotNull(result);
            verify(tenantSectionRepository, times(2)).findByRecId(1);
        }
    }

    @Test
    public void deleteTenantSection_Success() {
        Boolean result = tenantSectionService.deleteTenantSection(1, 0);

        assertTrue(result);
        verify(tenantSectionRepository).deleteById(1L);
    }

    @Test
    public void getOptionList_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(tenantSectionDetailOptionRepository.findByOptionNameAndTenant("testOption", mockTenant))
                    .thenReturn(new ArrayList<>());

            List<OptionDto> result = tenantSectionService.getOptionList("testOption");

            assertNotNull(result);
            verify(tenantSectionDetailOptionRepository).findByOptionNameAndTenant("testOption", mockTenant);
        }
    }
}
