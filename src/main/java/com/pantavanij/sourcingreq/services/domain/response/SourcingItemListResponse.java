package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.InstanceApproverHeaderDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourcingItemListResponse {
    private ApprovalStatusDto approvalStatus;
    private RequestStatusDto requestStatus;
    private List<SourcingItemDto> sourcingItemList;
    private List<InstanceApproverHeaderDto> approverHeaders;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
