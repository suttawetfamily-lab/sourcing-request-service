package com.pantavanij.sourcingreq.services.domain.dto.requester;

import com.pantavanij.sourcingreq.services.domain.dto.reviewer.ReviewerRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class RequesterRequestSearchDto {
    private List<RequesterRequestDto> requestDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
