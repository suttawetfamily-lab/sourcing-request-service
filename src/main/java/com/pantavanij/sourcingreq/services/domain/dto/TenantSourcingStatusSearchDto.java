package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantSourcingStatusSearchDto {
    private List<TenantSourcingStatusDto> tenantSourcingStatusList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
