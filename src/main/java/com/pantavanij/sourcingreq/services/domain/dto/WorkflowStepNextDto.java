package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class WorkflowStepNextDto {
    private Long recordId;
    @JsonIgnore
    private String tenantId;
    @JsonIgnore
    private String projectId;
    private Long workflowStepId;
    @NotNull
    private Integer stepNextTo;

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
