package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestRequesterDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestRequesterRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequesterSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthRequesterResponse;

import java.util.List;

public interface RequestRequesterService {
    List<RequestRequesterDto> saveRequestRequester(RequestRequesterRequest requestRequesterRequest, EPAuthRequesterResponse response);
    List<RequestRequesterDto> findByRequest(Long requestId);
    EPAuthRequesterResponse getRequesterListByConditions(RequesterSearchRequest request, String[] privilegeCodes);
    void deleteRequesterByRequestId(Long requestId);
}
