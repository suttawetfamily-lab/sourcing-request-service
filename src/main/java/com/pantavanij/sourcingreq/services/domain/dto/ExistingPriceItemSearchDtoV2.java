package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.util.List;


@Data
public class ExistingPriceItemSearchDtoV2 {
    private List<ExistingPriceItemDtoV2> existingPriceItemDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
