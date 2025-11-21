package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class WorkflowApproverDto {
    private Long recordId;
    @JsonIgnore
    private String projectId;
    @JsonIgnore
    private String tenantId;
    private Long workflowStepId;
    @NotEmpty
    private String referApproverId;
    private Boolean isRequired;
    @JsonIgnore
    @CreatedDate
    private Date createdAt;
    @JsonIgnore
    @CreatedBy
    private String createdBy;

    @JsonIgnore
    @LastModifiedDate
    private Date updatedAt;
    @JsonIgnore
    @LastModifiedBy
    private String updatedBy;

    private String referDelegatedApproverId;
}
