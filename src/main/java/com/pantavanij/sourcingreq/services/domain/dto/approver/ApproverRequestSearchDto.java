package com.pantavanij.sourcingreq.services.domain.dto.approver;

import lombok.Data;

import java.util.List;

@Data
public class ApproverRequestSearchDto {
    private List<ApproverRequestDto> requestDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
