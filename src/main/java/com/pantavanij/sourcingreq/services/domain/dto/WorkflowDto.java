package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pantavanij.sourcingreq.services.enums.WorkflowTemplateStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class WorkflowDto {

    private Long recordId;
    @NotNull
    private String workflowName;
    @NotNull
    private String tenantId;
    @NotNull
    private String projectId;
    private String description;
    @Enumerated(EnumType.STRING)
    private WorkflowTemplateStatus status;
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
    private Boolean isAdhoc;

    @Transient
    private List<WorkflowParamDto> params;

    @Transient
    private List<WorkflowStageDto> stages;

}
