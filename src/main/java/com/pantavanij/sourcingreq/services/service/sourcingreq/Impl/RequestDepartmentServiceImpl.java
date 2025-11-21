package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Department;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestDepartment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestDepartmentKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.DepartmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestDepartmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestProjectRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestDepartmentServiceImpl implements RequestDepartmentService {

    private final RequestDepartmentRepository requestDepartmentRepository;
    private final RequestProjectRepository requestProjectRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(String departmentId, Request request) {
        Optional<RequestDepartment> existingRequestDepartment = requestDepartmentRepository.findTop1ByRequestId(request.getRecId());
        if (existingRequestDepartment.isPresent()) {
            if (existingRequestDepartment.get().getDepartment().getRecId() == Integer.parseInt(departmentId)) return;
            requestDepartmentRepository.delete(existingRequestDepartment.get());
        }
        if (departmentId == null) return;
        requestProjectRepository.deleteByRequest(request);

        Department department = departmentRepository.findById(Integer.parseInt(departmentId))
                .orElseThrow(() -> new BusinessException(ApiMessage.E7025, ApiMessage.E7025.description()));

        RequestDepartment requestDepartment = RequestDepartment.builder()
                .id(new RequestDepartmentKey())
                .request(request)
                .department(department)
                .departmentCode(department.getCode())
                .departmentName(department.getName())
                .build();

        requestDepartmentRepository.save(requestDepartment);
    }
}
