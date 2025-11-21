package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestHistoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestHistory;
import com.pantavanij.sourcingreq.services.domain.request.RequestHistoryRequest;

import java.util.List;

public interface RequestHistoryService {

    List<RequestHistoryDto> getRequestHistoryByRequestID(Long requestID);

    List<RequestHistoryDto> getAllRequestHistoryByRequestID(Long requestID);

    RequestHistory saveRequestHistory(RequestHistoryRequest requestHistoryRequest);

    RequestHistory saveRequestHistoryByAction(Request request, Integer activityId);

    RequestHistory saveRequestHistoryByAction(Request request, Integer activityId, String remark);
}
