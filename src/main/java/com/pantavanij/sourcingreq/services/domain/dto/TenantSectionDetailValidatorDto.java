package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantSectionDetailValidatorDto {
    private ValidatorDto validator;
    private String name;
    private String value;
    private Integer sequence;
}
