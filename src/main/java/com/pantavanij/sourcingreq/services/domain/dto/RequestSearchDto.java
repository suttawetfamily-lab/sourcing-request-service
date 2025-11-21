package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.domain.dto.reviewer.ReviewerRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class RequestSearchDto {
    private List<ReviewerRequestDto> requestDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
