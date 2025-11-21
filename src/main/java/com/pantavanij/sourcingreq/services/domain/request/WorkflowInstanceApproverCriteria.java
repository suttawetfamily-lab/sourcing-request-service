package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class WorkflowInstanceApproverCriteria {
    @NotNull
//    @ApiModelProperty(required = true)
    private Integer pageNumber;
    @Min(1)
    @Max(100)
    @NotNull
//    @ApiModelProperty(required = true)
    private Integer pageSize;
    private List<String> sortBy;
    private List<String> sortDirection;
    private List<String> refDocumentId;
    private List<String> status;
    private List<String> referApproverId;
    private List<Long> workflowInstanceId;
    @JsonIgnore
    private List<Long> workflowId;
    private List<String> approvalType;
}
