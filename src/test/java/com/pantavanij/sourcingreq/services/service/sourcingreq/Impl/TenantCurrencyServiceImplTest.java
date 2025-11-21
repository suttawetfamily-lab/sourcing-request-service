package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import com.pantavanij.sourcingreq.services.domain.request.CurrencyRequest;
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

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenantCurrencyServiceImplTest {

    @InjectMocks
    private TenantCurrencyServiceImpl tenantCurrencyService;

    @Mock
    private TenantCurrencyRepository tenantCurrencyRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private UaaService uaaService;

    private Tenant mockTenant;
    private Currency mockCurrency;
    private TenantCurrency mockTenantCurrency;
    private UserDto mockUser;

    @Before
    public void setup() {
        mockTenant = new Tenant();
        mockTenant.setRecId(1);
        mockTenant.setCode("TEST_TENANT");

        mockCurrency = new Currency();
        mockCurrency.setRecId(1);
        mockCurrency.setCode("USD");
        mockCurrency.setName("US Dollar");

        TenantCurrencyKey tenantCurrencyKey = TenantCurrencyKey.builder()
                .tenantId(1)
                .currencyId(1)
                .build();


        mockTenantCurrency = new TenantCurrency();
        mockTenantCurrency.setId(tenantCurrencyKey);
        mockTenantCurrency.setTenant(mockTenant);
        mockTenantCurrency.setCurrency(mockCurrency);
        mockTenantCurrency.setSequence(1);
        mockTenantCurrency.setActive(true);
    }

    @Test
    public void getByCurrencyId_Success() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMockedStatic.when(AppUtil::getUser).thenReturn(mockUser);

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");
            when(tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(1, 1))
                    .thenReturn(Optional.of(mockTenantCurrency));

            TenantCurrencyDto result = tenantCurrencyService.getByCurrencyId(1);

            assertNotNull(result);
            verify(tenantCurrencyRepository).findByTenantRecIdAndCurrencyRecId(1, 1);
        }
    }

    @Test
    public void createTenantCurrency_Success() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMockedStatic.when(AppUtil::getUserName).thenReturn("testUser");

            CurrencyRequest request = new CurrencyRequest();
            request.setId(1);
            request.setSequence(1);
            request.setActive(true);

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(currencyRepository.findCurrenciesByRecId(1)).thenReturn(mockCurrency);
            when(tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(1, 1))
                    .thenReturn(Optional.empty());
            when(tenantCurrencyRepository.save(any(TenantCurrency.class)))
                    .thenReturn(mockTenantCurrency);

            Integer result = tenantCurrencyService.createTenantCurrency(request);

            assertNotNull(result);
            assertEquals(Integer.valueOf(1), result);
            verify(tenantCurrencyRepository).save(any(TenantCurrency.class));
        }
    }

    @Test
    public void updateTenantCurrency_Success() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");
            appUtilMockedStatic.when(AppUtil::getUserName).thenReturn("testUser");

            CurrencyRequest request = new CurrencyRequest();
            request.setId(1);
            request.setSequence(2);
            request.setActive(true);

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(currencyRepository.findCurrenciesByRecId(1)).thenReturn(mockCurrency);
            when(tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(1, 1))
                    .thenReturn(Optional.of(mockTenantCurrency));
            when(tenantCurrencyRepository.save(any(TenantCurrency.class)))
                    .thenReturn(mockTenantCurrency);

            Integer result = tenantCurrencyService.updateTenantCurrency(request);

            assertNotNull(result);
            assertEquals(Integer.valueOf(1), result);
            verify(tenantCurrencyRepository).save(any(TenantCurrency.class));
        }
    }

    @Test
    public void createTenantCurrency_CurrencyNotFound() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");

            CurrencyRequest request = new CurrencyRequest();
            request.setId(1);

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(currencyRepository.findCurrenciesByRecId(1)).thenReturn(null);

            Integer result = tenantCurrencyService.createTenantCurrency(request);

            assertEquals(Integer.valueOf(0), result);
            verify(tenantCurrencyRepository, never()).save(any(TenantCurrency.class));
        }
    }

    @Test
    public void createTenantCurrency_AlreadyExists() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");

            CurrencyRequest request = new CurrencyRequest();
            request.setId(1);

            when(tenantService.findByCode("TEST_TENANT")).thenReturn(mockTenant);
            when(currencyRepository.findCurrenciesByRecId(1)).thenReturn(mockCurrency);
            when(tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(1, 1))
                    .thenReturn(Optional.of(mockTenantCurrency));

            Integer result = tenantCurrencyService.createTenantCurrency(request);

            assertEquals(Integer.valueOf(-1), result);
            verify(tenantCurrencyRepository, never()).save(any(TenantCurrency.class));
        }
    }
}
