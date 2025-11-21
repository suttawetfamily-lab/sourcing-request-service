package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantSectionDetailWatchDto {
    private TenantSectionDetail tenantSectionDetail;
    private String fieldName;
    private String originalFieldName;
    private String updatedFieldName;
    private String values;
    private String groupName;
}
