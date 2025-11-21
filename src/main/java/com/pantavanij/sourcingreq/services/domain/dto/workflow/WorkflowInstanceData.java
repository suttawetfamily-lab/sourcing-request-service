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
public class WorkflowInstanceData {

    private String projectId;
    private String tenantId;
    private Long recordId;
    private Long workflowId;
    private String refDocumentId;
    private String viewDocumentUrl;
    private String status;
    private String mark;
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
    private Long parentId;
    private List<WorkflowInstanceStageDto> instanceStages;

}
