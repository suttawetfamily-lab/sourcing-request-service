package com.pantavanij.sourcingreq.services.domain.response;


import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingDto;
import com.pantavanij.sourcingreq.services.domain.dto.sourcingapprover.SourcingApproverRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcSourcingApproverListResponse {
    private List<ExcSourcingDto> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
