package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantUnit;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Unit;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import com.pantavanij.sourcingreq.services.domain.request.UnitRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantUnitRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.UnitRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenantUnitServiceImplTest {

    @InjectMocks
    private TenantUnitServiceImpl tenantUnitService;

    @Mock
    private TenantUnitRepository tenantUnitRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private UaaService uaaService;

    @Mock
    private UnitRepository unitRepository;

    private Tenant mockTenant;
    private Unit mockUnit;
    private TenantUnit mockTenantUnit;
    private UserDto mockUser;

    @Before
    public void setUp() {
        mockTenant = Tenant.builder()
                .recId(1)
                .code("TEST_TENANT")
                .build();

        mockUnit = Unit.builder()
                .recId(1)
                .code("TEST_UNIT")
                .build();

        TenantUnitKey tenantUnitKey = TenantUnitKey.builder()
                .tenantId(1)
                .unitId(1)
                .build();

        mockTenantUnit = TenantUnit.builder()
                .id(tenantUnitKey)
                .tenant(mockTenant)
                .unit(mockUnit)
                .sequence(1)
                .isDefault(true)
                .active(true)
                .build();

        mockUser = new UserDto();
        mockUser.setTenantId("TEST_TENANT");
        mockUser.setUsername("testUser");
        mockUser.setFullName("testUser");
    }

    @Test
    public void createTenantUnit_Success() {
        UnitRequest request = new UnitRequest();
        request.setId(1);
        request.setSequence(0);
        request.setDefault(true);
        request.setActive(true);

        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMockedStatic.when(AppUtil::getUserName).thenReturn(mockUser.getUsername());

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(unitRepository.findUnitByRecId(1)).thenReturn(Optional.of(mockUnit));
            when(tenantUnitRepository.findByTenantRecIdAndUnitRecId(1, 1)).thenReturn(Optional.empty());
            when(tenantUnitRepository.findFirstByTenantRecIdOrderBySequenceDesc(1)).thenReturn(Optional.empty());
            when(tenantUnitRepository.save(any(TenantUnit.class))).thenReturn(mockTenantUnit);

            Integer result = tenantUnitService.createTenantUnit(request);

            assertEquals(Integer.valueOf(1), result);
            verify(tenantUnitRepository).save(any(TenantUnit.class));
        }
    }

    @Test
    public void getByUnitId_Success() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMockedStatic.when(AppUtil::getUser).thenReturn(mockUser);

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(uaaService.getUserTimeZone(mockUser, null)).thenReturn("UTC");
            when(tenantUnitRepository.findByTenantRecIdAndUnitRecId(1, 1)).thenReturn(Optional.of(mockTenantUnit));

            TenantUnitDto result = tenantUnitService.getByUnitId(1);

            assertNotNull(result);
            verify(tenantUnitRepository).findByTenantRecIdAndUnitRecId(1, 1);
        }
    }

    @Test
    public void findTenantUnitByUnitIdAndTenantId_Success() {
        when(tenantUnitRepository.findTenantUnitByUnitIdAndTenantId(1, 1)).thenReturn(mockTenantUnit);

        TenantUnit result = tenantUnitService.findTenantUnitByUnitIdAndTenantId(1, 1);

        assertNotNull(result);
        assertEquals(mockTenantUnit, result);
        verify(tenantUnitRepository).findTenantUnitByUnitIdAndTenantId(1, 1);
    }

    @Test
    public void createTenantUnit_UnitNotFound_ReturnsZero() {
        UnitRequest request = new UnitRequest();
        request.setId(1);

        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(unitRepository.findUnitByRecId(1)).thenReturn(Optional.empty());

            Integer result = tenantUnitService.createTenantUnit(request);

            assertEquals(Integer.valueOf(0), result);
        }
    }

    @Test
    public void createTenantUnit_AlreadyExists_ReturnsMinusOne() {
        UnitRequest request = new UnitRequest();
        request.setId(1);

        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(unitRepository.findUnitByRecId(1)).thenReturn(Optional.of(mockUnit));
            when(tenantUnitRepository.findByTenantRecIdAndUnitRecId(1, 1)).thenReturn(Optional.of(mockTenantUnit));

            Integer result = tenantUnitService.createTenantUnit(request);

            assertEquals(Integer.valueOf(-1), result);
        }
    }

    @Test
    public void getByUnitId_NotFound_ReturnsNull() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(tenantUnitRepository.findByTenantRecIdAndUnitRecId(1, 1)).thenReturn(Optional.empty());

            TenantUnitDto result = tenantUnitService.getByUnitId(1);

            assertNull(result);
        }
    }
}
