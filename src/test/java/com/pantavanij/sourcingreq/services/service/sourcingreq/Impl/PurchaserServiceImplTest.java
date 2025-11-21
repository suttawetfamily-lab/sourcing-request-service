package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purchaser;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.PurchaserRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.PurchaserRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PurchaserServiceImplTest {

    @InjectMocks
    private PurchaserServiceImpl purchaserService;

    @Mock
    private EpAuthClient epAuthClient;

    @Mock
    private PurchaserRepository purchaserRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private UaaService uaaService;

    private Tenant mockTenant;
    private Purchaser mockPurchaser;
    private PurchaserRequest mockPurchaserRequest;
    private UserDto mockUser;

    @Before
    public void setUp() {
        mockTenant = Tenant.builder()
                .recId(1)
                .code("TEST_TENANT")
                .build();

        mockPurchaser = Purchaser.builder()
                .recId(1)
                .tenant(mockTenant)
                .purchaserName("Test Purchaser")
                .email("test@test.com")
                .sequence(1)
                .categoryPurchasers(new ArrayList<>())
                .subCategoryPurchasers(new ArrayList<>())
                .build();

        mockPurchaserRequest = new PurchaserRequest();
        mockPurchaserRequest.setRecId(1);
        mockPurchaserRequest.setPurchaserName("Test Purchaser");
        mockPurchaserRequest.setEmail("test@test.com");

        mockUser = new UserDto();
        mockUser.setUsername("testUser");
        mockUser.setEmail("test@test.com");
    }
    @Test
    public void testGetPurchaserByTenantIdAndSearchTermAndCategoryId() {
        List<Purchaser> purchaserList = new ArrayList<>();
        purchaserList.add(mockPurchaser);

        when(purchaserRepository.findByTenantIdAndCategoryIdAndCodeOrName(1, 1, "test"))
                .thenReturn(purchaserList);

        assertNotNull(purchaserService.getPurchaserByTenantIdAndSearchTermAndCategoryId(1, "test", 1));
    }

    @Test
    public void testCreatePurchaser() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMockedStatic.when(AppUtil::getUserName).thenReturn("testUser");

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");
            when(purchaserRepository.findFirstByTenantRecIdOrderBySequenceDesc(1))
                    .thenReturn(Optional.of(mockPurchaser));
            when(purchaserRepository.save(any(Purchaser.class))).thenReturn(mockPurchaser);

            PurchaserDto result = purchaserService.createPurchaser(mockPurchaserRequest);
            assertNotNull(result);
        }
    }

    @Test
    public void testUpdatePurchaser() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMockedStatic.when(AppUtil::getUserName).thenReturn("testUser");

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(purchaserRepository.findPurchaserByRecIdAndTenant(1, 1))
                    .thenReturn(Optional.of(mockPurchaser));
            when(purchaserRepository.save(any(Purchaser.class))).thenReturn(mockPurchaser);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");

            PurchaserDto result = purchaserService.updatePurchaser(mockPurchaserRequest);
            assertNotNull(result);
        }
    }

    @Test
    public void testDeletePurchaserByRecId() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(purchaserRepository.findPurchaserByRecIdAndTenant(1, 1))
                    .thenReturn(Optional.of(mockPurchaser));
            doNothing().when(purchaserRepository).deletePurchaserByRecId(1);

            boolean result = purchaserService.deletePurchaserByRecId(1);
            assertTrue(result);
        }
    }

    @Test
    public void testFindPurchaserByRecId() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMockedStatic.when(AppUtil::getUser).thenReturn(mockUser);

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");
            when(purchaserRepository.findPurchaserByRecIdAndTenant(1, 1))
                    .thenReturn(Optional.of(mockPurchaser));

            PurchaserDto result = purchaserService.findPurchaserByRecId(1);
            assertNotNull(result);
        }
    }

    @Test
    public void testGetByTenantId() {
        List<Purchaser> purchaserList = new ArrayList<>();
        purchaserList.add(mockPurchaser);

        when(purchaserRepository.findByTenantId(1)).thenReturn(purchaserList);

        List<Purchaser> result = purchaserService.getByTenantId(1);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
