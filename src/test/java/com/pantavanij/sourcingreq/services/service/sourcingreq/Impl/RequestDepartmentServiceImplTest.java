package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.DepartmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestDepartmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestProjectRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestDepartmentService;
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
public class RequestDepartmentServiceImplTest {
    private RequestDepartmentRepository requestDepartmentRepository = mock(RequestDepartmentRepository.class);
    private RequestProjectRepository requestProjectRepository = mock(RequestProjectRepository.class);
    private DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
    private RequestDepartmentService requestDepartmentService =
            new RequestDepartmentServiceImpl(requestDepartmentRepository, requestProjectRepository, departmentRepository);

    @Captor
    private ArgumentCaptor<RequestDepartment> requestDepartmentCaptor;

    @Test
    public void hasRequestDepartment_saveOrUpdate_deleteExistingThenSaveNew() {
        Integer newDepartmentId = 2;
        Integer existingDepartmentId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Department newDepartmentMock = Department.builder().recId(newDepartmentId).code("DEP_CODE_2").name("DEP_NAME_2").build();
        Department existingDepartmentMock =
                Department.builder().recId(existingDepartmentId).code("DEP_CODE_1").name("DEP_NAME_1").build();

        RequestDepartment requestDepartmentMock = RequestDepartment.builder()
                .request(requestMock)
                .department(existingDepartmentMock)
                .build();

        when(requestDepartmentRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestDepartmentMock));
        when(departmentRepository.findById(newDepartmentId)).thenReturn(Optional.of(newDepartmentMock));

        requestDepartmentService.saveOrUpdate(newDepartmentId.toString(), requestMock);

        verify(requestDepartmentRepository, times(1)).delete(requestDepartmentMock);
        verify(requestDepartmentRepository, times(1)).save(requestDepartmentCaptor.capture());
        RequestDepartment actualResult = requestDepartmentCaptor.getValue();

        assertEquals(newDepartmentMock.getCode(), actualResult.getDepartmentCode());
        assertEquals(newDepartmentMock.getName(), actualResult.getDepartmentName());
        assertEquals(newDepartmentId, actualResult.getDepartment().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void noRequestDepartment_saveOrUpdate_SaveNew() {
        Integer departmentId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Department departmentMock = Department.builder().recId(departmentId).code("DEP_CODE").name("DEP_NAME").build();

        when(requestDepartmentRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.empty());
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentMock));

        requestDepartmentService.saveOrUpdate(departmentId.toString(), requestMock);

        verify(requestDepartmentRepository, never()).delete(any());
        verify(requestDepartmentRepository, times(1)).save(requestDepartmentCaptor.capture());
        RequestDepartment actualResult = requestDepartmentCaptor.getValue();

        assertEquals(departmentMock.getCode(), actualResult.getDepartmentCode());
        assertEquals(departmentMock.getName(), actualResult.getDepartmentName());
        assertEquals(departmentId, actualResult.getDepartment().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void sameRequestDepartment_saveOrUpdate_doNothing() {
        Integer departmentId = 1;
        Integer existingDepartmentId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Department departmentMock = Department.builder().recId(existingDepartmentId).code("DEP_CODE").name("DEP_NAME").build();

        RequestDepartment requestDepartmentMock = RequestDepartment.builder()
                .request(requestMock)
                .department(departmentMock)
                .build();

        when(requestDepartmentRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestDepartmentMock));

        requestDepartmentService.saveOrUpdate(departmentId.toString(), requestMock);

        verify(requestDepartmentRepository, never()).delete(any());
        verify(requestDepartmentRepository, never()).save(any());
    }

    @Test
    public void departmentNotFound_saveOrUpdate_throwBusinessException() {
        Integer departmentId = 1;
        Integer existingDepartmentId = 2;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Department departmentMock = Department.builder().recId(existingDepartmentId).code("DEP_CODE").name("DEP_NAME").build();

        RequestDepartment requestDepartmentMock = RequestDepartment.builder()
                .request(requestMock)
                .department(departmentMock)
                .build();

        when(requestDepartmentRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestDepartmentMock));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestDepartmentService.saveOrUpdate(departmentId.toString(), requestMock));
    }

    @Test
    public void departmentIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newDepartmentId = null;
        Integer existingDepartmentId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Department existingDepartmentMock = Department.builder().recId(existingDepartmentId).code("DEP_CODE").name("DEP_NAME").build();

        RequestDepartment requestDepartmentMock = RequestDepartment.builder()
                .request(requestMock)
                .department(existingDepartmentMock)
                .departmentCode(existingDepartmentMock.getCode())
                .departmentName(existingDepartmentMock.getName())
                .build();

        when(requestDepartmentRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestDepartmentMock));

        requestDepartmentService.saveOrUpdate(newDepartmentId.toString(), requestMock);

        verify(requestDepartmentRepository, times(1)).delete(requestDepartmentCaptor.capture());
        verify(requestDepartmentRepository, never()).save(any());
        RequestDepartment actualResult = requestDepartmentCaptor.getValue();

        assertEquals(existingDepartmentMock.getCode(), actualResult.getDepartmentCode());
        assertEquals(existingDepartmentMock.getName(), actualResult.getDepartmentName());
        assertEquals(existingDepartmentId, actualResult.getDepartment().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }
}