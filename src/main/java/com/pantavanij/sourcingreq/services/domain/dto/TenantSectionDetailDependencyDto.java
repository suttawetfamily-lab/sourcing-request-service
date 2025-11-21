package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantSectionDetailDependencyDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TenantSectionDetail tenantSectionDetail;
    private String name;
    private String value;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String groupName;
    private String action;
}
