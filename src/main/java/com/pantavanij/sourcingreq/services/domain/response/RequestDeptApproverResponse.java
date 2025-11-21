package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.RequestDeptApproverDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestReviewerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDeptApproverResponse {
    private ApiResponseStatus status;

    private List<RequestDeptApproverDto> requestDeptApproverDtos;

    public RequestDeptApproverResponse(List<RequestDeptApproverDto> requestDeptApproverDtos) {
        this.requestDeptApproverDtos = requestDeptApproverDtos;
        this.status = new ApiResponseStatus();
    }
}
