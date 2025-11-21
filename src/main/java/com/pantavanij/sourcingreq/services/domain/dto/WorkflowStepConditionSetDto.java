package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pantavanij.sourcingreq.services.enums.OperationType;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class WorkflowStepConditionSetDto {
    private Long recordId;
    @JsonIgnore
    private String projectId;
    @JsonIgnore
    private String tenantId;
    private Long workflowStepId;
    @NotNull
    private Integer conditionSetNumber;
    @NotEmpty
    private String paramName;
    @NotNull
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    private String paramValue;

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
}
