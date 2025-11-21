package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestHistoryResponse {

    private ApiResponseStatus status;

    private Long requestHistoryId;
    private Long requestId;
    private Integer tenantId;
    private Integer activityId;

    public RequestHistoryResponse(RequestHistory data) {
        this.requestHistoryId = data.getRecId();
        this.requestId = data.getRequest().getRecId();
        this.tenantId = data.getTenant().getRecId();
        this.activityId = data.getActivity().getRecId();
        this.status = new ApiResponseStatus();
    }
}
