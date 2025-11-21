package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pantavanij.sourcingreq.services.domain.dto.WorkflowParamDto;
import com.pantavanij.sourcingreq.services.domain.dto.WorkflowStageDto;
import com.pantavanij.sourcingreq.services.enums.WorkflowTemplateStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class WorkflowRequest {
    private Long recordId;
    @JsonIgnore
    private String tenantId;
    @NotEmpty
    @ApiModelProperty(required = true)
    private String workflowName;
    private String description;
    @NotNull
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(required = true)
    private WorkflowTemplateStatus status;
    @Valid
    private List<WorkflowParamDto> params;
    @NotEmpty
    @Valid
    @ApiModelProperty(required = true)
    private List<WorkflowStageDto> stages;
    private Boolean isAdhoc = false;
}
