package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourcingGridFieldSearchableDto {
    private String code;
    private String displayName;
    private Integer sequence;
    private String tenantCode;
}
