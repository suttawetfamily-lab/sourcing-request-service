package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.EPAuthReviewerDto;
import com.pantavanij.sourcingreq.services.domain.dto.ReportLineDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDefaultReportLineResponse {
    private ApiResponseStatus status;

    private EPAuthReviewerDto data;

    public RequestDefaultReportLineResponse(EPAuthReviewerDto data) {
        this.data = data;
        this.status = new ApiResponseStatus();
    }
}
