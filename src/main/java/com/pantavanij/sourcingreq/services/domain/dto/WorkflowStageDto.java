package com.pantavanij.sourcingreq.services.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class WorkflowStageDto {
    private Long recordId;
    private Long workflowId;
    @NotEmpty
    @ApiModelProperty(required = true)
    private String stageName;
    @NotNull
    @Min(1)
    @ApiModelProperty(required = true)
    private Integer sequence;

    @ApiModelProperty(example = "PARALLEL, SEQUENTIAL")
    private String stageType;

    @NotEmpty
    @Valid
    @ApiModelProperty(required = true)
    private List<WorkflowStepDto> steps;

    @NotEmpty
    private String displayLane;

    WorkflowStageDto() {

    }

    public WorkflowStageDto(
            Long recordId,
            Long workflowId,
            String stageName,
            Integer sequence,
            String displayLane,
            List<WorkflowStepDto> steps
    ) {
        this.recordId = recordId;
        this.workflowId = workflowId;
        this.stageName = stageName;
        this.sequence = sequence;
        this.displayLane = displayLane;
        this.steps = steps;
    }
}
