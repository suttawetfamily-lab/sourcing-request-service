package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestDeptApproverDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestApprover;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthDeptApproverResponse;

import java.util.List;

public interface RequestDeptApproverService {
    void saveRequestDeptApprover(Request request, Tenant tenant, List<Integer> approvers);

    List<RequestDeptApproverDto> findByRequest(Long requestId);

    RequestDeptApproverDto findCurrentAwaitingDeptApprover(Long requestId);

    RequestDeptApproverDto findLatestApprovedDeptApprover(Long requestId);

    RequestDeptApproverDto find1StCancelledDeptApprover(Long requestId);

    List<RequestDeptApproverDto> findAllApprovedDeptApprover(Long requestId);

    List<Long> findAllRelatedRequestIds();

//    List<Long> findRequestReviewerByReviewerName(String reviewerName);
    EPAuthDeptApproverResponse getDeptApproverListByConditions(DeptApproverSearchRequest request);

    EPAuthDeptApproverResponse getDeptApproverListByConditions(DeptApproverSearchRequest request, String[] privilegeCodes);

    void deleteByRequestId(Long requestId);

    boolean hasApprovalPermission(RequestApprover requestApprover);

    boolean isLastDeptApprover(Long requestId);
}
