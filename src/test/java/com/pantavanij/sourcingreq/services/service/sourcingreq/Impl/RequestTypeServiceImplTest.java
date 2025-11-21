package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TypeRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestTypeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestTypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestTypeServiceImplTest {
    private RequestTypeRepository requestTypeRepository = mock(RequestTypeRepository.class);
    private TypeRepository typeRepository = mock(TypeRepository.class);
    private RequestTypeService requestTypeService =
            new RequestTypeServiceImpl(requestTypeRepository, typeRepository);

    @Captor
    private ArgumentCaptor<RequestType> requestTypeCaptor;

    @Test
    public void hasRequestType_saveOrUpdate_deleteExistingThenSaveNew() {
        Integer newTypeId = 2;
        Integer existingTypeId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Type newTypeMock = Type.builder().recId(newTypeId).code("TYPE_CODE_2").name("TYPE_NAME_2").build();
        Type existingTypeMock = Type.builder().recId(existingTypeId).code("TYPE_CODE_1").name("TYPE_NAME_1").build();

        RequestType requestTypeMock = RequestType.builder()
                .request(requestMock)
                .type(existingTypeMock)
                .build();

        when(requestTypeRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestTypeMock));
        when(typeRepository.findById(newTypeId)).thenReturn(Optional.of(newTypeMock));

        requestTypeService.saveOrUpdate(newTypeId, requestMock);

        verify(requestTypeRepository, times(1)).delete(requestTypeMock);
        verify(requestTypeRepository, times(1)).save(requestTypeCaptor.capture());
        RequestType actualResult = requestTypeCaptor.getValue();

        assertEquals(newTypeMock.getCode(), actualResult.getTypeCode());
        assertEquals(newTypeMock.getName(), actualResult.getTypeName());
        assertEquals(newTypeId, actualResult.getType().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void noRequestType_saveOrUpdate_SaveNew() {
        Integer typeId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Type typeMock = Type.builder().recId(typeId).code("TYPE_CODE").name("TYPE_NAME").build();

        when(requestTypeRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.empty());
        when(typeRepository.findById(typeId)).thenReturn(Optional.of(typeMock));

        requestTypeService.saveOrUpdate(typeId, requestMock);

        verify(requestTypeRepository, never()).delete(any());
        verify(requestTypeRepository, times(1)).save(requestTypeCaptor.capture());
        RequestType actualResult = requestTypeCaptor.getValue();

        assertEquals(typeMock.getCode(), actualResult.getTypeCode());
        assertEquals(typeMock.getName(), actualResult.getTypeName());
        assertEquals(typeId, actualResult.getType().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void sameRequestType_saveOrUpdate_doNothing() {
        Integer typeId = 1;
        Integer existingTypeId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Type typeMock = Type.builder().recId(existingTypeId).code("TYPE_CODE").name("TYPE_NAME").build();

        RequestType requestTypeMock = RequestType.builder()
                .request(requestMock)
                .type(typeMock)
                .build();

        when(requestTypeRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestTypeMock));

        requestTypeService.saveOrUpdate(typeId, requestMock);

        verify(requestTypeRepository, never()).delete(any());
        verify(requestTypeRepository, never()).save(any());
    }

    @Test
    public void typeNotFound_saveOrUpdate_throwBusinessException() {
        Integer typeId = 2;
        Integer existingTypeId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Type typeMock = Type.builder().recId(existingTypeId).code("TYPE_CODE").name("TYPE_NAME").build();

        RequestType requestTypeMock = RequestType.builder()
                .request(requestMock)
                .type(typeMock)
                .build();

        when(requestTypeRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestTypeMock));
        when(typeRepository.findById(typeId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestTypeService.saveOrUpdate(typeId, requestMock));
    }

    @Test
    public void typeIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newTypeId = null;
        Integer existingTypeId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Type existingTypeMock =
                Type.builder().recId(existingTypeId).code("TYPE_CODE").name("TYPE_NAME").build();

        RequestType requestTypeMock = RequestType.builder()
                .request(requestMock)
                .type(existingTypeMock)
                .typeCode(existingTypeMock.getCode())
                .typeName(existingTypeMock.getName())
                .build();

        when(requestTypeRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestTypeMock));

        requestTypeService.saveOrUpdate(newTypeId, requestMock);

        verify(requestTypeRepository, times(1)).delete(requestTypeCaptor.capture());
        verify(requestTypeRepository, never()).save(any());
        RequestType actualResult = requestTypeCaptor.getValue();

        assertEquals(existingTypeMock.getCode(), actualResult.getTypeCode());
        assertEquals(existingTypeMock.getName(), actualResult.getTypeName());
        assertEquals(existingTypeId, actualResult.getType().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }
}