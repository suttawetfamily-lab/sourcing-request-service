package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.enums.WorkflowParamDataType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class WorkflowParamDto {
    private Long recordId;
    private Long workflowId;

    @NotEmpty
    @Size(min = 1, max = 1000)
    @ApiModelProperty(required = true)
    private String paramName;
    @NotNull
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(required = true)
    private WorkflowParamDataType dataType;
    private String defaultValue;

    WorkflowParamDto() {
    }

    public WorkflowParamDto(
            Long recordId,
            Long workflowId,
            String paramName,
            WorkflowParamDataType dataType,
            String defaultValue
    ) {
        this.recordId = recordId;
        this.workflowId = workflowId;
        this.paramName = paramName;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
    }
}
