package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantConfigSearchDto {
    private List<TenantConfigDto> tenantConfigList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
