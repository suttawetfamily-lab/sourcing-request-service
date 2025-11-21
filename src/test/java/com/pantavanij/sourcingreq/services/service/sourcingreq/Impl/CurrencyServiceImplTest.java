package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.domain.request.CurrencyMasterDataRequest;
import com.pantavanij.sourcingreq.services.domain.request.CurrencySearchRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.CurrencyRepository;
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
public class CurrencyServiceImplTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private UaaService uaaService;

    @Mock
    private UserDto mockUser;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private Currency currency;
    private List<Currency> currencyList;
    private static final Integer CURRENCY_ID = 1;
    private static final String TIME_ZONE = "Asia/Bangkok";

    @Before
    public void setUp() {
        currency = Currency.builder()
                .recId(1)
                .code("USD")
                .name("US Dollar")
                .build();

        currencyList = new ArrayList<>();
        currencyList.add(currency);
    }

    @Test
    public void getCurrencyByTenantIdAndSearchTerm_Success() {
        when(currencyRepository.findByTenantIdAndCodeOrDescription(anyInt(), anyString()))
                .thenReturn(currencyList);

        List<OptionDto> result = currencyService.getCurrencyByTenantIdAndSearchTerm(1, "USD");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void getCurrencyByTenantId_Success() {
        when(currencyRepository.findByTenantId(anyInt())).thenReturn(currencyList);

        List<OptionDto> result = currencyService.getCurrencyByTenantId(1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void getCurrencyById_Success() {
        when(currencyRepository.findByRecId(anyInt())).thenReturn(Optional.of(currency));

        CurrencyDto result = currencyService.getCurrencyById(1);

        assertNotNull(result);
        assertEquals("USD", currency.getCode());
    }

    @Test
    public void getAllCurrency_Success() {
        when(currencyRepository.findAll()).thenReturn(currencyList);

        List<CurrencyDto> result = currencyService.getAllCurrency();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void getCurrencyMasterDataById_Success() {
        // Arrange
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getUser).thenReturn(mockUser);
            when(uaaService.getUserTimeZone(mockUser, null)).thenReturn(TIME_ZONE);
            when(currencyRepository.findByRecId(CURRENCY_ID)).thenReturn(Optional.of(currency));

            // Act
            CurrencyMasterDto result = currencyService.getCurrencyMasterDataById(CURRENCY_ID);

            // Assert
            assertNotNull(result);
            assertEquals(CURRENCY_ID, result.getRecId());
            assertEquals("USD", result.getCode());
            assertEquals("US Dollar", result.getName());

            // Verify
            verify(currencyRepository).findByRecId(CURRENCY_ID);
            verify(uaaService).getUserTimeZone(mockUser, null);
        }
    }

    @Test
    public void getCurrencyMasterDataById_NotFound() {
        // Arrange
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getUser).thenReturn(mockUser);
            when(uaaService.getUserTimeZone(mockUser, null)).thenReturn(TIME_ZONE);
            when(currencyRepository.findByRecId(CURRENCY_ID)).thenReturn(Optional.empty());

            // Act
            CurrencyMasterDto result = currencyService.getCurrencyMasterDataById(CURRENCY_ID);

            // Assert
            assertNull(result);

            // Verify
            verify(currencyRepository).findByRecId(CURRENCY_ID);
            verify(uaaService).getUserTimeZone(mockUser, null);
        }
    }

    @Test
    public void getCurrencyMasterDataById_ThrowsException() {
        // Arrange
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getUser).thenReturn(mockUser);
            when(uaaService.getUserTimeZone(mockUser, null)).thenReturn(TIME_ZONE);
            when(currencyRepository.findByRecId(CURRENCY_ID)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    currencyService.getCurrencyMasterDataById(CURRENCY_ID)
            );
            assertEquals("Database error", exception.getMessage());

            // Verify
            verify(currencyRepository).findByRecId(CURRENCY_ID);
            verify(uaaService).getUserTimeZone(mockUser, null);
        }
    }

    @Test
    public void searchCurrencyListByCondition_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Currency> currencyPage = new PageImpl<>(currencyList);

        when(currencyRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(currencyPage);
        when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");

        CurrencySearchDto result = currencyService.searchCurrencyListByCondition(new CurrencySearchRequest(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(0, result.getPage());
    }

    @Test
    public void createCurrency_Success() {
        when(currencyRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        CurrencyMasterDataRequest request = new CurrencyMasterDataRequest();
        request.setCode("USD");
        request.setName("US Dollar");

        Integer result = currencyService.createCurrency(request);

        assertNotNull(result);
        assertEquals(currency.getRecId(), result);
    }

    @Test
    public void updateCurrency_Success() {
        when(currencyRepository.findByRecId(anyInt())).thenReturn(Optional.of(currency));
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        CurrencyMasterDataRequest request = new CurrencyMasterDataRequest();
        request.setRecId(1);
        request.setCode("USD");
        request.setName("US Dollar Updated");

        Integer result = currencyService.updateCurrency(request);

        assertNotNull(result);
        assertEquals(currency.getRecId(), result);
    }

    @Test
    public void deleteCurrencyById_Success() {
        when(currencyRepository.findByRecId(anyInt())).thenReturn(Optional.of(currency));
        doNothing().when(currencyRepository).deleteByRecId(anyInt());

        Integer result = currencyService.deleteCurrencyById(1);

        assertNotNull(result);
        assertEquals(currency.getRecId(), result);
        verify(currencyRepository, times(1)).deleteByRecId(anyInt());
    }

    @Test
    public void getCurrencyById_NotFound() {
        when(currencyRepository.findByRecId(anyInt())).thenReturn(Optional.empty());

        CurrencyDto result = currencyService.getCurrencyById(999);

        assertNull(result);
    }

    @Test
    public void createCurrency_AlreadyExists() {
        when(currencyRepository.findByCode(anyString())).thenReturn(Optional.of(currency));

        CurrencyMasterDataRequest request = new CurrencyMasterDataRequest();
        request.setCode("USD");
        request.setName("US Dollar");

        Integer result = currencyService.createCurrency(request);

        assertEquals(Integer.valueOf(-1), result);
    }
}