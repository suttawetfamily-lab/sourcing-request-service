package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.EPAuthReviewerDto;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.RequestReportLineDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestReportLineResponse {
    private ApiResponseStatus status;

    private List<EPAuthReviewerDto> data;

    public RequestReportLineResponse(List<EPAuthReviewerDto> data) {
        this.data = data;
        this.status = new ApiResponseStatus();
    }
}
