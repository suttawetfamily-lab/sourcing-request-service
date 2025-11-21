package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class WorkflowStepDto {
    private Long recordId;
    @JsonIgnore
    private String tenantId;
    @JsonIgnore
    private String projectId;
    private Long workflowStageId;
    private Integer orderNumber;
    @NotEmpty
    private String stepName;

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
    @NotNull
    @Min(1)
    private Integer numberOfApproverRequired;

    @Transient
    @Valid
    private List<WorkflowStepConditionSetDto> conditions;

    @Transient
    @Valid
    private List<WorkflowApproverDto> approvers;

    @Transient
    @Valid
    private List<WorkflowStepNextDto> nexts;

    private String approvalType;
}
