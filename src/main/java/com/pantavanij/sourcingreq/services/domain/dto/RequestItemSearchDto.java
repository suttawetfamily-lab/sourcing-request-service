package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.util.List;


@Data
public class RequestItemSearchDto {
    private List<RequestItemDto> requestItemDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
