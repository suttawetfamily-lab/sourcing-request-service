package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import java.util.*;

@Data
public class CurrencySearchDto {
    private List<CurrencyMasterDto> currencyList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
