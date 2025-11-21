package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import java.util.List;

public interface DepartmentService {

    List<DepartmentDto> getDepartmentBySearchTerm(String searchTerm, Integer organizationId);

    DepartmentDto getDepartmentByDepartmentCode(String departmentCode, Integer tenantId);

    List<DepartmentOptionDto> getDepartmentBySearchTerm(Integer tenantId, String searchTerm, Integer organizationId);

    DepartmentSearchDto searchDepartmentByCondition(DepartmentSearchRequest request, Pageable pageable);

    DepartmentDto findDepartmentByRecIdAndTenantId(Integer departmentId);

    DepartmentDto createDepartment(DepartmentRequest request);

    DepartmentDto updateDepartment(DepartmentRequest request);

    DepartmentDto updateDepartmentSequence(DepartmentSequenceRequest request);

    int deleteDepartmentByRecId(Integer departmentId);
}
