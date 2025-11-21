package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.reviewer.RequestReportLineDto;
import lombok.Data;

import java.util.List;

@Data
public class RequestReportLineCommentResponse {
    private ApiResponseStatus status;

    private List<RequestReportLineDto> requestReportLine;

    public RequestReportLineCommentResponse(List<RequestReportLineDto> requestReportLineDtos) {
        this.requestReportLine = requestReportLineDtos;
        this.status = new ApiResponseStatus();
    }
}
