package com.pantavanij.sourcingreq.services.domain.dto.reviewer;

import lombok.Data;

import java.util.List;

@Data
public class ReviewerRequestSearchDto {
    private List<ReviewerRequestDto> requestDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
