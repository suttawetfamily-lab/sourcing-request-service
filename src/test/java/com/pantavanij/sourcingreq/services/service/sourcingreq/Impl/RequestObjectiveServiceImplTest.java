package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ObjectiveRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestObjectiveRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestObjectiveService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestObjectiveServiceImplTest {

    private RequestObjectiveRepository requestObjectiveRepository = mock(RequestObjectiveRepository.class);
    private ObjectiveRepository objectiveRepository = mock(ObjectiveRepository.class);
    private RequestObjectiveService requestObjectiveService =
            new RequestObjectiveServiceImpl(requestObjectiveRepository, objectiveRepository);

    @Captor
    private ArgumentCaptor<RequestObjective> requestObjectiveCaptor;

    @Test
    public void hasRequestObjective_saveOrUpdate_deleteExistingThenSaveNew() {
        Integer newObjectiveId = 2;
        Integer existingObjectiveId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Objective newObjectiveMock = Objective.builder().recId(newObjectiveId).code("OBJ_CODE_2").name("OBJ_NAME_2").build();
        Objective existingObjectiveMock =
                Objective.builder().recId(existingObjectiveId).code("OBJ_CODE_1").name("OBJ_NAME_1").build();

        RequestObjective requestObjectiveMock = RequestObjective.builder()
                .request(requestMock)
                .objective(existingObjectiveMock)
                .build();

        when(requestObjectiveRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestObjectiveMock));
        when(objectiveRepository.findById(newObjectiveId)).thenReturn(Optional.of(newObjectiveMock));

        requestObjectiveService.saveOrUpdate(newObjectiveId, requestMock);

        verify(requestObjectiveRepository, times(1)).delete(requestObjectiveMock);
        verify(requestObjectiveRepository, times(1)).save(requestObjectiveCaptor.capture());
        RequestObjective actualResult = requestObjectiveCaptor.getValue();

        assertEquals(newObjectiveMock.getCode(), actualResult.getObjectiveCode());
        assertEquals(newObjectiveMock.getName(), actualResult.getObjectiveName());
        assertEquals(newObjectiveId, actualResult.getObjective().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void noRequestObjective_saveOrUpdate_SaveNew() {
        Integer objectiveId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Objective objectiveMock = Objective.builder().recId(objectiveId).code("OBJ_CODE").name("OBJ_NAME").build();

        when(requestObjectiveRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.empty());
        when(objectiveRepository.findById(objectiveId)).thenReturn(Optional.of(objectiveMock));

        requestObjectiveService.saveOrUpdate(objectiveId, requestMock);

        verify(requestObjectiveRepository, never()).delete(any());
        verify(requestObjectiveRepository, times(1)).save(requestObjectiveCaptor.capture());
        RequestObjective actualResult = requestObjectiveCaptor.getValue();

        assertEquals(objectiveMock.getCode(), actualResult.getObjectiveCode());
        assertEquals(objectiveMock.getName(), actualResult.getObjectiveName());
        assertEquals(objectiveId, actualResult.getObjective().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void sameRequestObjective_saveOrUpdate_doNothing() {
        Integer objectiveId = 1;
        Integer existingObjectiveId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Objective objectiveMock = Objective.builder().recId(existingObjectiveId).code("OBJ_CODE").name("OBJ_NAME").build();

        RequestObjective requestObjectiveMock = RequestObjective.builder()
                .request(requestMock)
                .objective(objectiveMock)
                .build();

        when(requestObjectiveRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestObjectiveMock));

        requestObjectiveService.saveOrUpdate(objectiveId, requestMock);

        verify(requestObjectiveRepository, never()).delete(any());
        verify(requestObjectiveRepository, never()).save(any());
    }

    @Test
    public void objectiveNotFound_saveOrUpdate_throwBusinessException() {
        Integer objectiveId = 2;
        Integer existingObjectiveId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Objective objectiveMock = Objective.builder().recId(existingObjectiveId).code("OBJ_CODE").name("OBJ_NAME").build();

        RequestObjective requestObjectiveMock = RequestObjective.builder()
                .request(requestMock)
                .objective(objectiveMock)
                .build();

        when(requestObjectiveRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestObjectiveMock));
        when(objectiveRepository.findById(objectiveId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestObjectiveService.saveOrUpdate(objectiveId, requestMock));
    }

    @Test
    public void objectiveIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newObjectiveId = null;
        Integer existingObjectiveId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Objective existingObjectiveMock =
                Objective.builder().recId(existingObjectiveId).code("OBJ_CODE").name("OBJ_NAME").build();

        RequestObjective requestObjectiveMock = RequestObjective.builder()
                .request(requestMock)
                .objective(existingObjectiveMock)
                .objectiveCode(existingObjectiveMock.getCode())
                .objectiveName(existingObjectiveMock.getName())
                .build();

        when(requestObjectiveRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestObjectiveMock));

        requestObjectiveService.saveOrUpdate(newObjectiveId, requestMock);

        verify(requestObjectiveRepository, times(1)).delete(requestObjectiveCaptor.capture());
        verify(requestObjectiveRepository, never()).save(any());
        RequestObjective actualResult = requestObjectiveCaptor.getValue();

        assertEquals(existingObjectiveMock.getCode(), actualResult.getObjectiveCode());
        assertEquals(existingObjectiveMock.getName(), actualResult.getObjectiveName());
        assertEquals(existingObjectiveId, actualResult.getObjective().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }
}