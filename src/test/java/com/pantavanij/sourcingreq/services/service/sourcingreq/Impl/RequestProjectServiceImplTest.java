package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ProjectRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestDepartmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestProjectRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestProjectService;
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
public class RequestProjectServiceImplTest {
    private RequestProjectRepository requestProjectRepository = mock(RequestProjectRepository.class);
    private RequestDepartmentRepository requestDepartmentRepository  = mock(RequestDepartmentRepository.class);
    private ProjectRepository projectRepository = mock(ProjectRepository.class);
    private RequestProjectService requestProjectService =
            new RequestProjectServiceImpl(requestProjectRepository, requestDepartmentRepository, projectRepository);

    @Captor
    private ArgumentCaptor<RequestProject> requestProjectCaptor;

    @Test
    public void hasRequestProject_saveOrUpdate_deleteExistingThenSaveNew() {
        Integer newProjectId = 2;
        Integer existingProjectId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Project newProjectMock = Project.builder().recId(newProjectId).code("PRO_CODE_2").name("PRO_NAME_2").build();
        Project existingProjectMock =
                Project.builder().recId(existingProjectId).code("PRO_CODE_1").name("PRO_NAME_1").build();

        RequestProject requestProjectMock = RequestProject.builder()
                .request(requestMock)
                .project(existingProjectMock)
                .build();

        when(requestProjectRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestProjectMock));
        when(projectRepository.findById(newProjectId)).thenReturn(Optional.of(newProjectMock));

        requestProjectService.saveOrUpdate(newProjectId.toString(), requestMock, 2);

        verify(requestProjectRepository, times(1)).delete(requestProjectMock);
        verify(requestProjectRepository, times(1)).save(requestProjectCaptor.capture());
        RequestProject actualResult = requestProjectCaptor.getValue();

        assertEquals(newProjectMock.getCode(), actualResult.getProjectCode());
        assertEquals(newProjectMock.getName(), actualResult.getProjectName());
        assertEquals(newProjectId, actualResult.getProject().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void noRequestProject_saveOrUpdate_SaveNew() {
        Integer projectId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Project projectMock = Project.builder().recId(projectId).code("PRO_CODE").name("PRO_NAME").build();

        when(requestProjectRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.empty());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectMock));

        requestProjectService.saveOrUpdate(projectId.toString(), requestMock, 2);

        verify(requestProjectRepository, never()).delete(any());
        verify(requestProjectRepository, times(1)).save(requestProjectCaptor.capture());
        RequestProject actualResult = requestProjectCaptor.getValue();

        assertEquals(projectMock.getCode(), actualResult.getProjectCode());
        assertEquals(projectMock.getName(), actualResult.getProjectName());
        assertEquals(projectId, actualResult.getProject().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void sameRequestProject_saveOrUpdate_doNothing() {
        Integer projectId = 1;
        Integer existingProjectId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Project projectMock = Project.builder().recId(existingProjectId).code("PRO_CODE").name("PRO_NAME").build();

        RequestProject requestProjectMock = RequestProject.builder()
                .request(requestMock)
                .project(projectMock)
                .build();

        when(requestProjectRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestProjectMock));

        requestProjectService.saveOrUpdate(projectId.toString(), requestMock, 2);

        verify(requestProjectRepository, never()).delete(any());
        verify(requestProjectRepository, never()).save(any());
    }

    @Test
    public void projectNotFound_saveOrUpdate_throwBusinessException() {
        Integer projectId = 1;
        Integer existingProjectId = 2;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Project projectMock = Project.builder().recId(existingProjectId).code("PRO_CODE").name("PRO_NAME").build();

        RequestProject requestProjectMock = RequestProject.builder()
                .request(requestMock)
                .project(projectMock)
                .build();

        when(requestProjectRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestProjectMock));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestProjectService.saveOrUpdate(projectId.toString(), requestMock, 2));
    }

    @Test
    public void projectIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newProjectId = null;
        Integer existingProjectId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Project existingProjectMock =
                Project.builder().recId(existingProjectId).code("PRO_CODE").name("PRO_NAME").build();

        RequestProject requestProjectMock = RequestProject.builder()
                .request(requestMock)
                .project(existingProjectMock)
                .projectCode(existingProjectMock.getCode())
                .projectName(existingProjectMock.getName())
                .build();

        when(requestProjectRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestProjectMock));

        requestProjectService.saveOrUpdate(newProjectId.toString(), requestMock, 2);

        verify(requestProjectRepository, times(1)).delete(requestProjectCaptor.capture());
        verify(requestProjectRepository, never()).save(any());
        RequestProject actualResult = requestProjectCaptor.getValue();

        assertEquals(existingProjectMock.getCode(), actualResult.getProjectCode());
        assertEquals(existingProjectMock.getName(), actualResult.getProjectName());
        assertEquals(existingProjectId, actualResult.getProject().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }
}