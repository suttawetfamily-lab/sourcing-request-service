package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Project;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestProject;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestProjectKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ProjectRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestDepartmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestProjectRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestProjectServiceImpl implements RequestProjectService {

    private final RequestProjectRepository requestProjectRepository;
    private final RequestDepartmentRepository requestDepartmentRepository;
    private final ProjectRepository projectRepository;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(String projectCode, Request request, Integer tenantId) {
        Optional<RequestProject> existingRequestProject = requestProjectRepository.findTop1ByRequestId(request.getRecId());
        if (existingRequestProject.isPresent()) {
            if (existingRequestProject.get().getProject().getCode().equalsIgnoreCase(projectCode)) return;
            requestProjectRepository.delete(existingRequestProject.get());
        }
        if (projectCode == null) return;
        requestDepartmentRepository.deleteByRequest(request);

        Project project = projectRepository.getProjectByProjectCode(projectCode, tenantId);
        if (null == project) {
            throw new BusinessException(ApiMessage.E7023, ApiMessage.E7023.description());
        };

        RequestProject requestProject = RequestProject.builder()
                .id(new RequestProjectKey())
                .request(request)
                .project(project)
                .projectCode(project.getCode())
                .projectName(project.getName())
                .build();

        requestProjectRepository.save(requestProject);
    }
}
