package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ProjectDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private ApiResponseStatus status;

    private List<ProjectDto> projectList;

    public ProjectResponse(List<ProjectDto> projectList) {
        this.projectList = projectList;
        this.status = new ApiResponseStatus();
    }
}
