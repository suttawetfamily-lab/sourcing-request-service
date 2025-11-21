package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantCurrencySearchDto {
    private List<TenantCurrencyDto> tenantCurrencyList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}