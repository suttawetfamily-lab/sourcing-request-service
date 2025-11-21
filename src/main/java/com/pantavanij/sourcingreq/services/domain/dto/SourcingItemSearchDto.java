package com.pantavanij.sourcingreq.services.domain.dto;
import lombok.Data;

import java.util.List;


@Data
public class SourcingItemSearchDto {

    private ApprovalStatusDto approvalStatus;
    private RequestStatusDto requestStatus;
    private List<SourcingItemDto> sourcingItemDtoList;
    private List<InstanceApproverHeaderDto> approverHeaders;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
