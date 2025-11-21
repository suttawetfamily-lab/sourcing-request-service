package com.pantavanij.sourcingreq.services.domain.response.approver;

import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproverRequestListResponse {
    private List<ApproverRequestDto> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
