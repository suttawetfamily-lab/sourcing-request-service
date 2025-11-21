package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproverSearchDto {
    private List<ApproverDto> approverDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}

