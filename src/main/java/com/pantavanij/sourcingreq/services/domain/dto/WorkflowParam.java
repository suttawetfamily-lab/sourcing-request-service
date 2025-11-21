package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class WorkflowParam {
    private String type;
    private String category;
    private String subCategory;
    private Double amount;
}
