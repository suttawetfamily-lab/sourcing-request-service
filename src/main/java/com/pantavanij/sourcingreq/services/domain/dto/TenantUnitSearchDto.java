package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantUnitSearchDto {
    private List<TenantUnitDto> tenantUnitList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}