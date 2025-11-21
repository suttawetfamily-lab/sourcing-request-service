package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectControllerTest {
    private ProjectService projectService = mock(ProjectService.class);
    private TenantService tenantService = mock(TenantService.class);
    private ProjectChangeLogHeaderService projectChangeLogHeaderService = mock(ProjectChangeLogHeaderService.class);
    private ProjectController projectController = new ProjectController(projectService, projectChangeLogHeaderService, tenantService);

    @Test
    public void getProjectBySearchTerm_success() {
        int tenantId = 1;
        String tenantCode = "TRUE";
        String searchTerm = "pro";
        Tenant mockTenant = Tenant.builder().recId(tenantId).code(tenantCode).build();
        List<OptionDto> projectOptions = Arrays.asList(
                OptionDto.builder().value("2").name("ProjectA").label("Project A").build(),
                OptionDto.builder().value("3").name("ProjectB").label("Project B").build()
        );

        when(tenantService.findByCode(tenantCode)).thenReturn(mockTenant);
        when(projectService.getProjectBySearchTerm(tenantId, searchTerm)).thenReturn(projectOptions);

        ResponseEntity actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = projectController.getProjectBySearchTerm(searchTerm, null);
        }

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(projectOptions));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getProjectBySearchTerm_tenantNotFound() {
        String tenantCode = "TRUE";
        String searchTerm = "pro";

        when(tenantService.findByCode(tenantCode)).thenReturn(null);

        Exception actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = assertThrows(BusinessException.class, () -> projectController.getProjectBySearchTerm(searchTerm, null));
        }

        assertEquals("E7016: Tenant is not found", actualResult.getMessage());
    }
}