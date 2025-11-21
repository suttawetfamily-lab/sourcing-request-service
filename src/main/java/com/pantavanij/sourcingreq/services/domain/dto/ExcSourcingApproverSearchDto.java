package com.pantavanij.sourcingreq.services.domain.dto;
import lombok.Data;

import java.util.List;

@Data
public class ExcSourcingApproverSearchDto {
    private List<ExcSourcingDto> excSourcingDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
