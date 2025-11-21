package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingPurchaserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcing;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcingPurchaser;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.PurchaserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthPurchaserResponse;

import java.util.List;

public interface ExcSourcingPurchaserService {
    void saveExcSourcingPurchaser(ExcSourcing excSourcing, Tenant tenant, List<Integer> approvers);

    List<ExcSourcingPurchaserDto> findByExcSourcing(Long requestId);

    ExcSourcingPurchaserDto findCurrentAwaitingPurchaser(Long requestId);

    ExcSourcingPurchaserDto findLatestApprovedPurchaser(Long requestId);

    ExcSourcingPurchaserDto find1StCancelledPurchaser(Long requestId);

    List<ExcSourcingPurchaserDto> findAllApprovedPurchaser(Long requestId);

    List<Long> findAllRelatedRequestIds();

//    List<Long> findRequestReviewerByReviewerName(String reviewerName);
    EPAuthPurchaserResponse getPurchaserListByConditions(PurchaserSearchRequest request);

    EPAuthPurchaserResponse getPurchaserListByConditions(PurchaserSearchRequest request, String[] privilegeCodes);

    void deleteByRequestId(Long requestId);

    boolean hasApprovalPermission(ExcSourcingPurchaser excSourcingPurchaser);

    boolean isLastPurchaser(Long requestId);
}
