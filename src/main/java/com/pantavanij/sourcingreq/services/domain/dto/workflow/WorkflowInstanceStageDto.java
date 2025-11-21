package com.pantavanij.sourcingreq.services.domain.dto.workflow;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.List;

@Data
public class WorkflowInstanceStageDto {
    private String projectId;
    private String tenantId;
    private Long recordId;
    private Long workflowStageId;
    private String stageName;
    private String stageType;
    private Integer sequence;
    private Long workflowInstanceId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @CreatedDate
    private Date createdAt;
    @CreatedBy
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @LastModifiedDate
    private Date updatedAt;
    @LastModifiedBy
    private String updatedBy;
    private String displayLane;
    private List<WorkflowInstanceStepDto> instanceSteps;

}
