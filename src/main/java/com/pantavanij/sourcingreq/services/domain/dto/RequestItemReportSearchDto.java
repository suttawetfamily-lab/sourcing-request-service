package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestItemReportSearchDto {
    private List<RequestItemReportDto> requestItemReportDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}

