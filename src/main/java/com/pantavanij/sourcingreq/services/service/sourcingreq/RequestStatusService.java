package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;

import java.util.List;

public interface RequestStatusService {

    List<RequestStatusDto> getRequestStatusList();

    void updateRequestAndApprovalStatus(List<RequestItem> requestItemList, Request request);

    void updateRequestAndApprovalStatus(List<RequestItem> requestItemList, List<Request> requestList);
}
