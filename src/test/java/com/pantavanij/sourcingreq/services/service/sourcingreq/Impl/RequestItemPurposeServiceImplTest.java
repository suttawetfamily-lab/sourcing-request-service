package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purpose;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemPurpose;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.PurposeRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemPurposeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemPurposeService;
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
public class RequestItemPurposeServiceImplTest {

    private RequestItemPurposeRepository requestItemPurposeRepository = mock(RequestItemPurposeRepository.class);
    private PurposeRepository purposeRepository = mock(PurposeRepository.class);
    private RequestItemPurposeService requestItemPurposeService =
            new RequestItemPurposeServiceImpl(requestItemPurposeRepository, purposeRepository);

    @Captor
    private ArgumentCaptor<RequestItemPurpose> requestItemPurposeCaptor;

    @Test
    public void hasRequestItemPurpose_saveOrUpdate_deleteExistingThenSaveNew() {
        Integer newPurposeId = 2;
        Integer existingPurposeId = 1;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Purpose newPurposeMock = Purpose.builder().recId(newPurposeId).code("PURPOSE_2").name("PURPOSE_2").build();
        Purpose existingPurposeMock =
                Purpose.builder().recId(existingPurposeId).code("PURPOSE_1").name("PURPOSE_1").build();

        RequestItemPurpose requestItemPurposeMock = RequestItemPurpose.builder()
                .requestItem(requestItemMock)
                .purpose(existingPurposeMock)
                .build();

        when(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemPurposeMock));
        when(purposeRepository.findById(newPurposeId)).thenReturn(Optional.of(newPurposeMock));

        requestItemPurposeService.saveOrUpdate(newPurposeId, requestItemMock);

        verify(requestItemPurposeRepository, times(1)).delete(requestItemPurposeMock);
        verify(requestItemPurposeRepository, times(1)).save(requestItemPurposeCaptor.capture());
        RequestItemPurpose actualResult = requestItemPurposeCaptor.getValue();

        assertEquals(newPurposeMock.getCode(), actualResult.getPurposeCode());
        assertEquals(newPurposeMock.getName(), actualResult.getPurposeName());
        assertEquals(newPurposeId, actualResult.getPurpose().getRecId());
        assertEquals(requestItemId, actualResult.getRequestItem().getRecId());
    }

    @Test
    public void noRequestItemPurpose_saveOrUpdate_SaveNew() {
        Integer purposeId = 1;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Purpose purposeMock = Purpose.builder().recId(purposeId).code("PURPOSE").name("PURPOSE").build();

        when(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.empty());
        when(purposeRepository.findById(purposeId)).thenReturn(Optional.of(purposeMock));

        requestItemPurposeService.saveOrUpdate(purposeId, requestItemMock);

        verify(requestItemPurposeRepository, never()).delete(any());
        verify(requestItemPurposeRepository, times(1)).save(requestItemPurposeCaptor.capture());
        RequestItemPurpose actualResult = requestItemPurposeCaptor.getValue();

        assertEquals(purposeMock.getCode(), actualResult.getPurposeCode());
        assertEquals(purposeMock.getName(), actualResult.getPurposeName());
        assertEquals(purposeId, actualResult.getPurpose().getRecId());
        assertEquals(requestItemId, actualResult.getRequestItem().getRecId());
    }

    @Test
    public void sameRequestItemPurpose_saveOrUpdate_doNothing() {
        Integer purposeId = 1;
        Integer existingPurposeId = 1;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Purpose purposeMock = Purpose.builder().recId(existingPurposeId).code("PURPOSE").name("PURPOSE").build();

        RequestItemPurpose requestItemPurposeMock = RequestItemPurpose.builder()
                .requestItem(requestItemMock)
                .purpose(purposeMock)
                .build();

        when(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemPurposeMock));

        requestItemPurposeService.saveOrUpdate(purposeId, requestItemMock);

        verify(requestItemPurposeRepository, never()).delete(any());
        verify(requestItemPurposeRepository, never()).save(any());
    }

    @Test
    public void purposeNotFound_saveOrUpdate_throwBusinessException() {
        Integer purposeId = 1;
        Integer existingPurposeId = 2;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Purpose purposeMock = Purpose.builder().recId(existingPurposeId).code("PURPOSE").name("PURPOSE").build();

        RequestItemPurpose requestItemPurposeMock = RequestItemPurpose.builder()
                .requestItem(requestItemMock)
                .purpose(purposeMock)
                .build();

        when(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemPurposeMock));
        when(purposeRepository.findById(purposeId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestItemPurposeService.saveOrUpdate(purposeId, requestItemMock));
    }

    @Test
    public void purposeIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newPurposeId = null;
        Integer existingPurposeId = 1;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        Purpose existingPurposeMock =
                Purpose.builder().recId(existingPurposeId).code("PURPOSE").name("PURPOSE").build();

        RequestItemPurpose requestItemPurposeMock = RequestItemPurpose.builder()
                .requestItem(requestItemMock)
                .purpose(existingPurposeMock)
                .purposeCode(existingPurposeMock.getCode())
                .purposeName(existingPurposeMock.getName())
                .build();

        when(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemPurposeMock));

        requestItemPurposeService.saveOrUpdate(newPurposeId, requestItemMock);

        verify(requestItemPurposeRepository, times(1)).delete(requestItemPurposeCaptor.capture());
        verify(requestItemPurposeRepository, never()).save(any());
        RequestItemPurpose actualResult = requestItemPurposeCaptor.getValue();

        assertEquals(existingPurposeMock.getCode(), actualResult.getPurposeCode());
        assertEquals(existingPurposeMock.getName(), actualResult.getPurposeName());
        assertEquals(existingPurposeId, actualResult.getPurpose().getRecId());
        assertEquals(requestItemId, actualResult.getRequestItem().getRecId());
    }
}