package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingRequestVisibleConfig;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestPurchaser;
import com.pantavanij.sourcingreq.services.domain.request.ApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.DeptApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.PurchaserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthDeptApproverResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthPurchaserResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthReviewerResponse;

import java.util.List;

public interface RequestPurchaserService {

    boolean initializeRequestApprover();

    SourcingRequestVisibleConfig getApprovalAllTapConfig();

    EPAuthReviewerResponse getApproverListByConditions(ApproverSearchRequest request);

    EPAuthPurchaserResponse getPurchaserListByConditions(PurchaserSearchRequest request, String[] privilegeCodes);

    List<RequestPurchaser> getRequestPurchaserByRequest(Request request);

    void saveOrUpdate(Integer purchaserId, Request request);
}
