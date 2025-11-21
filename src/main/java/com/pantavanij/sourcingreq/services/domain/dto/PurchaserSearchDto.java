package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaserSearchDto {
    private List<PurchaserDto> purchaserList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}

