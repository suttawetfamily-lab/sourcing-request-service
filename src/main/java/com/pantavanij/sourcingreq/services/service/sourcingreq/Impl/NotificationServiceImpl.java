package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.ApprovalStatus.APPROVAL_AWAITING;
import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.DEPT_APPROVAL_AWAITING;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final RequestRepository requestRepository;
    private final TenantService tenantService;
    private final DelegationService delegationService;
    private final ExcSourcingRepository excSourcingRepository;

    @Override
    public Integer getCountApproverTask() {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        DelegatorByDelegateeRequest delegationActiveRequest = new DelegatorByDelegateeRequest();
        delegationActiveRequest.setDelegateeUserName(AppUtil.getUserName());

        // Delegation-status
        // 1=Upcoming
        // 2=Active
        // 3=Expired
        // 4=Cancelled
        delegationActiveRequest.setDelegationStatuses(List.of(2L));
        DelegatorByDelegateeResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);

        try {
            int totalCount = 0;
            // Count task of Department approver.
            if ((AppUtil.isDeptApprover() && AppUtil.isPurchaser()) || AppUtil.isDeptApprover()) {
                // Case delegation is active
                Integer delegatorCount = 0;
                if (delegationActiveResponse.getDelegatorUserNames() != null && !delegationActiveResponse.getDelegatorUserNames().isEmpty()) {
                    String delegatorName = delegationActiveResponse.getDelegatorUserNames().get(0);
                    delegatorCount = requestRepository.countDeptApproverTask(tenant.getRecId(), delegatorName, DEPT_APPROVAL_AWAITING.id());
                }

                Integer deptCount = requestRepository.countDeptApproverTask(tenant.getRecId(), AppUtil.getUserName(), DEPT_APPROVAL_AWAITING.id());

                totalCount += (delegatorCount + deptCount);

                Integer excDeptCount = excSourcingRepository.countDeptApproverExcSourcingTask(
                        tenant.getRecId(), AppUtil.getUserName(), DEPT_APPROVAL_AWAITING.id());
                totalCount += (excDeptCount != null ? excDeptCount : 0);
                return totalCount;

            // Count task of Purchaser.
            } else if (AppUtil.isPurchaser()) {
                // Case delegation is active
                Integer delegatorCount = 0;
                if (delegationActiveResponse.getDelegatorUserNames() != null && !delegationActiveResponse.getDelegatorUserNames().isEmpty()) {
                    String delegatorName = delegationActiveResponse.getDelegatorUserNames().get(0);
                    delegatorCount = requestRepository.countPurchaserTask(tenant.getRecId(), delegatorName, APPROVAL_AWAITING.id());
                }

                Integer purchaserCount = requestRepository.countPurchaserTask(tenant.getRecId(), AppUtil.getUserName(), APPROVAL_AWAITING.id());

                totalCount += (delegatorCount + purchaserCount);

                Integer excPurchaserCount = excSourcingRepository.countPurchaserExcSourcingTask(
                        tenant.getRecId(), AppUtil.getUserName(), DEPT_APPROVAL_AWAITING.id());
                totalCount += (excPurchaserCount != null ? excPurchaserCount : 0);
                return totalCount;
            }
            return null;
        } catch (Exception ex) {
            return -1;
        }
    }

    @Override
    public Integer getCountReviewerTask() {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
//            Integer rePortLineCount = requestRepository.countReportLineTask(tenant.getRecId(), AppUtil.getUserName());
//            Integer reviewerCount = requestRepository.countReviewerTask(tenant.getRecId(), AppUtil.getUserName());
            return requestRepository.countReviewerAndReportLineTask(tenant.getRecId(), AppUtil.getUserName());
        } catch (Exception ex) {
            return -1;
        }
    }

}
