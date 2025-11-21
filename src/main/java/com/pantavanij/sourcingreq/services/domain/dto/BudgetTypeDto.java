package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BudgetTypeDto {

    private Integer recId;
//    private Tenant tenant;
    private String name;
    private String description;
    private String budgetGroup;
    private boolean visible;
    private String createdBy;
    private Timestamp createdDate;
}
