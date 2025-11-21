package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO สำหรับ ExcSourcing search response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcSourcingApproverItemDto {
    private String sectionName;
    private List<Integer> approvers;
}
