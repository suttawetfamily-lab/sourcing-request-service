package com.pantavanij.sourcingreq.services.domain.dto;

import java.util.List;

public class WorkflowInstanceApproverPageDto {
    private List<WorkflowInstApproverDto> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalItems;
    private Integer totalPages;

    public WorkflowInstanceApproverPageDto(
            List<WorkflowInstApproverDto> content,
            Integer pageNumber,
            Integer pageSize,
            Long totalItems,
            Integer totalPages) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }
}
