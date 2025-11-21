package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.AdminClient;
import com.pantavanij.sourcingreq.services.client.BifrostClient;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.ProjectDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Project;
import com.pantavanij.sourcingreq.services.domain.mapper.ProjectMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import com.pantavanij.sourcingreq.util.TestUtil;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceImplTest {

    private ProjectRepository projectRepository = mock(ProjectRepository.class);
    private ProjectChangeLogHeaderRepository projectChangeLogHeaderRepository = mock(ProjectChangeLogHeaderRepository.class);
    private ProjectChangeLogDetailRepository projectChangeLogDetailRepository = mock(ProjectChangeLogDetailRepository.class);
    private TenantConfigRepository tenantConfigRepositor = mock(TenantConfigRepository.class);
    private FileUtil fileUtil = mock(FileUtil.class);
    private UaaService uaaService = mock(UaaService.class);
    private BifrostClient bifrostClient = mock(BifrostClient.class);
    private TenantService tenantService = mock(TenantService.class);
    private ProjectServiceImpl projectService = new ProjectServiceImpl(bifrostClient, fileUtil, projectRepository,projectChangeLogHeaderRepository, projectChangeLogDetailRepository, tenantService, uaaService, tenantConfigRepositor);
    private ProjectServiceImpl spyProjectService = spy(projectService);

    @Test
    public void getProjectBySearchTerm_success() {
        Integer tenantId = 1;
        String searchTerm = "search";
        String mockTimeZone = TestUtil.getMockTimeZone();
        Integer limit = 10;

        List<Project> mockProjectList = Arrays.asList(
                Project.builder().recId(1).code("01").name("Project A").build()
        );

        when(projectRepository.getProjectByTenantIdAndSearchTerm(tenantId, searchTerm, limit)).thenReturn(mockProjectList);
        when(uaaService.getUserTimeZone(any(), any())).thenReturn(mockTimeZone);

        List<ProjectDto> actualResult = projectService.getProjectBySearchTermV1(tenantId, searchTerm);

        List<ProjectDto> expectedResult = ProjectMapper.INSTANCE.toProjectDtoList(mockProjectList, mockTimeZone);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getProjectByDepartmentCode_success() {
        String projectCode = "01";
        Integer tenantId = 1;
        String mockTimeZone = TestUtil.getMockTimeZone();

        Project mockProject = Project.builder().recId(1).code("01").name("Project A").build();

        when(projectRepository.getProjectByProjectCode(projectCode, tenantId)).thenReturn(mockProject);
        when(uaaService.getUserTimeZone(any(), any())).thenReturn(mockTimeZone);

        ProjectDto actualResult = projectService.getProjectByProjectCode(projectCode, tenantId);

        ProjectDto expectedResult = ProjectMapper.INSTANCE.toProjectDto(mockProject, mockTimeZone);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getProjectByTenantIdAndSearchTerm_success() {
        int tenantId = 1;
        String searchTerm = "PRO";
        Integer limit = 10;
        List<Project> projectMock = Arrays.asList(
                Project.builder().recId(1).code("01").name("Project 1").build(),
                Project.builder().recId(2).code("02").name("Project 2").build(),
                Project.builder().recId(3).code("03").name("Project 3").build()
        );

        when(projectRepository.getProjectByTenantIdAndSearchTerm(tenantId, searchTerm, limit))
                .thenReturn(projectMock);

        List<OptionDto> actualResult = projectService.getProjectBySearchTerm(tenantId, searchTerm);

        List<OptionDto> expectedResult = Arrays.asList(
                OptionDto.builder().value("01").name("Project 1").label("01-Project 1").build(),
                OptionDto.builder().value("02").name("Project 2").label("02-Project 2").build(),
                OptionDto.builder().value("03").name("Project 3").label("03-Project 3").build()
        );
        Assertions.assertEquals(expectedResult, actualResult);
    }
}
