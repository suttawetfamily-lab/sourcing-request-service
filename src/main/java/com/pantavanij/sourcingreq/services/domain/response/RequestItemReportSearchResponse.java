package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestItemReportSearchResponse {
    private ApiResponseStatus status;
    private List<RequestItemReportDto> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}

