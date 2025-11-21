package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemCurrency;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.CurrencyRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemCurrencyRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemCurrencyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestItemCurrencyServiceImplTest {

    private RequestItemCurrencyRepository requestItemCurrencyRepository = mock(RequestItemCurrencyRepository.class);
    private CurrencyRepository currencyRepository = mock(CurrencyRepository.class);
    private RequestItemCurrencyService requestItemCurrencyService =
            new RequestItemCurrencyServiceImpl(requestItemCurrencyRepository, currencyRepository);

    @Captor
    private ArgumentCaptor<RequestItemCurrency> requestItemCurrencyCaptor;

    @Test
    public void hasRequestItemCurrency_saveOrUpdate_deleteExistingThenSaveNew() {
        Integer newCurrencyId = 2;
        Integer existingCurrencyId = 1;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Currency newCurrencyMock = Currency.builder().recId(newCurrencyId).code("THB_2").name("THB_2").build();
        Currency existingCurrencyMock =
                Currency.builder().recId(existingCurrencyId).code("THB_1").name("THB_1").build();

        RequestItemCurrency requestItemCurrencyMock = RequestItemCurrency.builder()
                .requestItem(requestItemMock)
                .currency(existingCurrencyMock)
                .build();

        when(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemCurrencyMock));
        when(currencyRepository.findById(newCurrencyId)).thenReturn(Optional.of(newCurrencyMock));

        requestItemCurrencyService.saveOrUpdate(newCurrencyId, requestItemMock);

        verify(requestItemCurrencyRepository, times(1)).delete(requestItemCurrencyMock);
        verify(requestItemCurrencyRepository, times(1)).save(requestItemCurrencyCaptor.capture());
        RequestItemCurrency actualResult = requestItemCurrencyCaptor.getValue();

        assertEquals(newCurrencyMock.getCode(), actualResult.getCurrencyCode());
        assertEquals(newCurrencyMock.getName(), actualResult.getCurrencyName());
        assertEquals(newCurrencyId, actualResult.getCurrency().getRecId());
        assertEquals(requestItemId, actualResult.getRequestItem().getRecId());
    }

    @Test
    public void noRequestItemCurrency_saveOrUpdate_SaveNew() {
        Integer currencyId = 1;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Currency currencyMock = Currency.builder().recId(currencyId).code("THB").name("THB").build();

        when(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.empty());
        when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currencyMock));

        requestItemCurrencyService.saveOrUpdate(currencyId, requestItemMock);

        verify(requestItemCurrencyRepository, never()).delete(any());
        verify(requestItemCurrencyRepository, times(1)).save(requestItemCurrencyCaptor.capture());
        RequestItemCurrency actualResult = requestItemCurrencyCaptor.getValue();

        assertEquals(currencyMock.getCode(), actualResult.getCurrencyCode());
        assertEquals(currencyMock.getName(), actualResult.getCurrencyName());
        assertEquals(currencyId, actualResult.getCurrency().getRecId());
        assertEquals(requestItemId, actualResult.getRequestItem().getRecId());
    }

    @Test
    public void sameRequestItemCurrency_saveOrUpdate_doNothing() {
        Integer currencyId = 1;
        Integer existingCurrencyId = 1;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Currency currencyMock = Currency.builder().recId(existingCurrencyId).code("THB").name("THB").build();

        RequestItemCurrency requestItemCurrencyMock = RequestItemCurrency.builder()
                .requestItem(requestItemMock)
                .currency(currencyMock)
                .build();

        when(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemCurrencyMock));

        requestItemCurrencyService.saveOrUpdate(currencyId, requestItemMock);

        verify(requestItemCurrencyRepository, never()).delete(any());
        verify(requestItemCurrencyRepository, never()).save(any());
    }

    @Test
    public void currencyNotFound_saveOrUpdate_throwBusinessException() {
        Integer currencyId = 1;
        Integer existingCurrencyId = 2;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Currency currencyMock = Currency.builder().recId(existingCurrencyId).code("THB").name("THB").build();

        RequestItemCurrency requestItemCurrencyMock = RequestItemCurrency.builder()
                .requestItem(requestItemMock)
                .currency(currencyMock)
                .build();

        when(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemCurrencyMock));
        when(currencyRepository.findById(currencyId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestItemCurrencyService.saveOrUpdate(currencyId, requestItemMock));
    }

    @Test
    public void currencyIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newCurrencyId = null;
        Integer existingCurrencyId = 1;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Currency existingCurrencyMock =
                Currency.builder().recId(existingCurrencyId).code("THB").name("THB").build();

        RequestItemCurrency requestItemCurrencyMock = RequestItemCurrency.builder()
                .requestItem(requestItemMock)
                .currency(existingCurrencyMock)
                .currencyCode(existingCurrencyMock.getCode())
                .currencyName(existingCurrencyMock.getName())
                .build();

        when(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemCurrencyMock));

        requestItemCurrencyService.saveOrUpdate(newCurrencyId, requestItemMock);

        verify(requestItemCurrencyRepository, times(1)).delete(requestItemCurrencyCaptor.capture());
        verify(requestItemCurrencyRepository, never()).save(any());
        RequestItemCurrency actualResult = requestItemCurrencyCaptor.getValue();

        assertEquals(existingCurrencyMock.getCode(), actualResult.getCurrencyCode());
        assertEquals(existingCurrencyMock.getName(), actualResult.getCurrencyName());
        assertEquals(existingCurrencyId, actualResult.getCurrency().getRecId());
        assertEquals(requestItemId, actualResult.getRequestItem().getRecId());
    }
}