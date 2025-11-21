package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import org.springframework.core.io.*;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.*;

import java.io.*;
import java.util.*;

public interface ProjectService {
    List<ProjectDto> getProjectBySearchTermV1(Integer tenantId, String searchTerm);

    ProjectDto getProjectByProjectCode(String projectCode, Integer tenantId);

    OptionDto getProjectOptionByProjectCode(String projectCode, Integer tenantId);

    void saveProjectBySchedule(String uri, Tenant tenant);

    List<OptionDto> getProjectBySearchTerm(Integer tenantId, String searchTerm);

    ProjectDto getProjectByRecId(Integer projectId);

    ProjectSearchDto searchProjectByCondition(ProjectSearchRequest request, Pageable pageable);

    UploadProjectFileResponse validateProjectFileUpload(MultipartFile file) throws IOException;

    ProjectChangeLogHeaderDto saveProjectData(List<Project> projectList, MultipartFile file);

    ProjectChangeLogHeaderSearchDto searchProjectChangeLogHeaderByCondition(ProjectChangeLogHeaderSearchRequest request, Pageable pageable);

    ByteArrayResource downloadProjectTemplate();

    Integer deleteProjectByRecId(Integer recId);

    ProjectDto updateProject(ProjectRequest request);

    ProjectDto updateProjectSequence(SequenceRequest request);
}
