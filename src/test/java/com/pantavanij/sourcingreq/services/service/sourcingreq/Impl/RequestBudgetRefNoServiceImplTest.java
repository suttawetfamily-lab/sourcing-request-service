package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.BudgetRefNo;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestBudgetRefNo;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.BudgetRefNoRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestBudgetRefNoRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestBudgetRefNoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class RequestBudgetRefNoServiceImplTest {
    private RequestBudgetRefNoRepository requestBudgetRefNoRepository = mock(RequestBudgetRefNoRepository.class);
    private BudgetRefNoRepository budgetRefNoRepository = mock(BudgetRefNoRepository.class);
    private RequestBudgetRefNoService requestBudgetRefNoService =
            new RequestBudgetRefNoServiceImpl(requestBudgetRefNoRepository, budgetRefNoRepository);

    @Captor
    private ArgumentCaptor<RequestBudgetRefNo> requestBudgetRefNoCaptor;

    @Test
    public void hasRequestBudgetRefNo_saveOrUpdate_deleteExistingThenSaveNew() {
        Integer newBudgetRefNoId = 2;
        Integer existingBudgetRefNoId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        BudgetRefNo newBudgetRefNoMock = BudgetRefNo.builder().recId(newBudgetRefNoId).code("BUDGET_REF_NO_CODE_2").name("BUDGET_REF_NO_NAME_2").build();
        BudgetRefNo existingBudgetRefNoMock =
                BudgetRefNo.builder().recId(existingBudgetRefNoId).code("BUDGET_REF_NO_CODE_1").name("BUDGET_REF_NO_NAME_1").build();

        RequestBudgetRefNo requestBudgetRefNoMock = RequestBudgetRefNo.builder()
                .request(requestMock)
                .budgetRefNo(existingBudgetRefNoMock)
                .build();

        when(requestBudgetRefNoRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestBudgetRefNoMock));
        when(budgetRefNoRepository.findById(newBudgetRefNoId)).thenReturn(Optional.of(newBudgetRefNoMock));

        requestBudgetRefNoService.saveOrUpdate(newBudgetRefNoId, requestMock);

        verify(requestBudgetRefNoRepository, times(1)).delete(requestBudgetRefNoMock);
        verify(requestBudgetRefNoRepository, times(1)).save(requestBudgetRefNoCaptor.capture());
        RequestBudgetRefNo actualResult = requestBudgetRefNoCaptor.getValue();

        assertEquals(newBudgetRefNoMock.getCode(), actualResult.getBudgetRefNoCode());
        assertEquals(newBudgetRefNoMock.getName(), actualResult.getBudgetRefNoName());
        assertEquals(newBudgetRefNoId, actualResult.getBudgetRefNo().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void noRequestBudgetRefNo_saveOrUpdate_SaveNew() {
        Integer budgetRefNoId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        BudgetRefNo budgetRefNoMock = BudgetRefNo.builder().recId(budgetRefNoId).code("BUDGET_REF_NO_CODE").name("BUDGET_REF_NO_NAME").build();

        when(requestBudgetRefNoRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.empty());
        when(budgetRefNoRepository.findById(budgetRefNoId)).thenReturn(Optional.of(budgetRefNoMock));

        requestBudgetRefNoService.saveOrUpdate(budgetRefNoId, requestMock);

        verify(requestBudgetRefNoRepository, never()).delete(any());
        verify(requestBudgetRefNoRepository, times(1)).save(requestBudgetRefNoCaptor.capture());
        RequestBudgetRefNo actualResult = requestBudgetRefNoCaptor.getValue();

        assertEquals(budgetRefNoMock.getCode(), actualResult.getBudgetRefNoCode());
        assertEquals(budgetRefNoMock.getName(), actualResult.getBudgetRefNoName());
        assertEquals(budgetRefNoId, actualResult.getBudgetRefNo().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void sameRequestBudgetRefNo_saveOrUpdate_doNothing() {
        Integer budgetRefNoId = 1;
        Integer existingBudgetRefNoId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        BudgetRefNo budgetRefNoMock = BudgetRefNo.builder().recId(existingBudgetRefNoId).code("BUDGET_REF_NO_CODE").name("BUDGET_REF_NO_NAME").build();

        RequestBudgetRefNo requestBudgetRefNoMock = RequestBudgetRefNo.builder()
                .request(requestMock)
                .budgetRefNo(budgetRefNoMock)
                .build();

        when(requestBudgetRefNoRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestBudgetRefNoMock));

        requestBudgetRefNoService.saveOrUpdate(budgetRefNoId, requestMock);

        verify(requestBudgetRefNoRepository, never()).delete(any());
        verify(requestBudgetRefNoRepository, never()).save(any());
    }

    @Test
    public void budgetRefNoNotFound_saveOrUpdate_throwBusinessException() {
        Integer budgetRefNoId = 1;
        Integer existingBudgetRefNoId = 2;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        BudgetRefNo budgetRefNoMock = BudgetRefNo.builder().recId(existingBudgetRefNoId).code("BUDGET_REF_NO_CODE").name("BUDGET_REF_NO_NAME").build();

        RequestBudgetRefNo requestBudgetRefNoMock = RequestBudgetRefNo.builder()
                .request(requestMock)
                .budgetRefNo(budgetRefNoMock)
                .build();

        when(requestBudgetRefNoRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestBudgetRefNoMock));
        when(budgetRefNoRepository.findById(budgetRefNoId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestBudgetRefNoService.saveOrUpdate(budgetRefNoId, requestMock));
    }

    @Test
    public void budgetRefNoIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newBudgetRefNoId = null;
        Integer existingBudgetRefNoId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        BudgetRefNo existingBudgetRefNoMock =
                BudgetRefNo.builder().recId(existingBudgetRefNoId).code("BUDGET_REF_NO_CODE").name("BUDGET_REF_NO_NAME").build();

        RequestBudgetRefNo requestBudgetRefNoMock = RequestBudgetRefNo.builder()
                .request(requestMock)
                .budgetRefNo(existingBudgetRefNoMock)
                .budgetRefNoCode(existingBudgetRefNoMock.getCode())
                .budgetRefNoName(existingBudgetRefNoMock.getName())
                .build();

        when(requestBudgetRefNoRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestBudgetRefNoMock));

        requestBudgetRefNoService.saveOrUpdate(newBudgetRefNoId, requestMock);

        verify(requestBudgetRefNoRepository, times(1)).delete(requestBudgetRefNoCaptor.capture());
        verify(requestBudgetRefNoRepository, never()).save(any());
        RequestBudgetRefNo actualResult = requestBudgetRefNoCaptor.getValue();

        assertEquals(existingBudgetRefNoMock.getCode(), actualResult.getBudgetRefNoCode());
        assertEquals(existingBudgetRefNoMock.getName(), actualResult.getBudgetRefNoName());
        assertEquals(existingBudgetRefNoId, actualResult.getBudgetRefNo().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }
}