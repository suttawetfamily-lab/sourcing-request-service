package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingDeptApproverDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.DeptApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthDeptApproverResponse;

import java.util.List;

public interface ExcSourcingDeptApproverService {
    void saveExcSourcingDeptApprover(ExcSourcing excSourcing, Tenant tenant, List<Integer> approvers);

    List<ExcSourcingDeptApproverDto> findByExcSourcing(Long requestId);

    ExcSourcingDeptApproverDto findCurrentAwaitingDeptApprover(Long requestId);

    ExcSourcingDeptApproverDto findLatestApprovedDeptApprover(Long requestId);

    ExcSourcingDeptApproverDto find1StCancelledDeptApprover(Long requestId);

    List<ExcSourcingDeptApproverDto> findAllApprovedDeptApprover(Long requestId);

    List<Long> findAllRelatedRequestIds();

//    List<Long> findRequestReviewerByReviewerName(String reviewerName);
    EPAuthDeptApproverResponse getDeptApproverListByConditions(DeptApproverSearchRequest request);

    EPAuthDeptApproverResponse getDeptApproverListByConditions(DeptApproverSearchRequest request, String[] privilegeCodes);

    void deleteByRequestId(Long requestId);

    boolean hasApprovalPermission(ExcSourcingApprover requestApprover);

    boolean isLastDeptApprover(Long requestId);
}
