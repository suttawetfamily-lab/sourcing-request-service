package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.DepartmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.DepartmentOptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Department;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.mapper.DepartmentMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DepartmentService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.Impl.DepartmentServiceImpl;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.util.TestUtil;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DepartmentServiceImplTest {

    private DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
    private RequestDepartmentRepository requestDepartmentRepository;
    private TenantService tenantService = mock(TenantService.class);
    private UaaService uaaService = mock(UaaService.class);
    private DepartmentService departmentService =
            new DepartmentServiceImpl(departmentRepository, requestDepartmentRepository, tenantService, uaaService);

    @Test
    public void getDepartmentBySearchTerm_success() {
        Integer tenantId = 1;
        String tenantCode = "TRUE";
        String searchTerm = "10000";
        Integer organizationId = 1;
        String mockTimeZone = TestUtil.getMockTimeZone();

        List<Department> mockDepartmentList = Arrays.asList(
                Department.builder().recId(1).code("10000").name("10000-PRESIDENT OFFICE").build()
        );

        when(tenantService.findByCode(tenantCode)).thenReturn(Tenant.builder().recId(tenantId).build());
        when(departmentRepository.getDepartmentByTenantIdAndSearchTerm(tenantId, searchTerm, organizationId)).thenReturn(mockDepartmentList);
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);

        List<DepartmentDto> actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = departmentService.getDepartmentBySearchTerm(searchTerm, organizationId);
        }

        List<DepartmentDto> expectedResult =
                DepartmentMapper.INSTANCE.toDepartmentDtoList(mockDepartmentList, mockTimeZone);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getDepartmentBySearchTerm_successWithEmpty() {
        Integer tenantId = 1;
        String tenantCode = "TRUE";
        String searchTerm = "10000";
        Integer organizationId = 1;
        String mockTimeZone = TestUtil.getMockTimeZone();

        when(tenantService.findByCode(tenantCode)).thenReturn(Tenant.builder().recId(tenantId).build());
        when(departmentRepository.getDepartmentByTenantIdAndSearchTerm(tenantId, searchTerm, organizationId))
                .thenReturn(Collections.emptyList());
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);

        List<DepartmentDto> actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = departmentService.getDepartmentBySearchTerm(searchTerm, organizationId);
        }

        assertEquals(Collections.emptyList(), actualResult);
    }

    @Test
    public void getDepartmentByDepartmentCode_success() {
        String departmentCode = "10000";
        Integer tenantId = 1;
        String mockTimeZone = TestUtil.getMockTimeZone();
        Timestamp mockTimestamp = TestUtil.getMockTimestamp();

        Department mockDepartment = Department.builder()
                .recId(1)
                .code("10000")
                .name("10000-PRESIDENT OFFICE")
                .createdDate(mockTimestamp)
                .build();

        when(departmentRepository.getDepartmentByDepartmentCode(departmentCode, tenantId)).thenReturn(mockDepartment);
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);

        DepartmentDto actualResult = departmentService.getDepartmentByDepartmentCode(departmentCode, tenantId);

        DepartmentDto expectedResult =
                DepartmentMapper.INSTANCE.toDepartmentDto(mockDepartment, mockTimeZone);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getDepartmentByDepartmentCode_successWithNull() {
        String departmentCode = "10000";
        Integer tenantId = 1;
        String mockTimeZone = TestUtil.getMockTimeZone();

        when(departmentRepository.getDepartmentByDepartmentCode(departmentCode, tenantId)).thenReturn(null);
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);

        DepartmentDto actualResult = departmentService.getDepartmentByDepartmentCode(departmentCode, tenantId);

        assertEquals(null, actualResult);
    }

    @Test
    public void getDepartmentByTenantIdAndSearchTerm_success() {
        int tenantId = 1;
        String searchTerm = "DEP";
        Integer organizationId = 1;

        List<Department> departmentMock = Arrays.asList(
                Department.builder().recId(1).code("DEP1").name("Department 1").build(),
                Department.builder().recId(2).code("DEP2").name("Department 2").build(),
                Department.builder().recId(3).code("DEP3").name("Department 3").build()
        );

        when(departmentRepository.getDepartmentByTenantIdAndSearchTerm(tenantId, searchTerm, organizationId))
                .thenReturn(departmentMock);

        List<DepartmentOptionDto> actualResult = departmentService.getDepartmentBySearchTerm(tenantId, searchTerm, organizationId);

        List<DepartmentOptionDto> expectedResult = DepartmentMapper.INSTANCE.toDepartmentOptionDto(departmentMock);
        Assertions.assertEquals(expectedResult, actualResult);
    }

}