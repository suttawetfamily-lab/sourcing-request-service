package com.pantavanij.sourcingreq.services.domain.dto;


import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantSectionSearchDto {
    private List<TenantSectionDto> tenantSectionList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
