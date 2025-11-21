package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.approver.ForwardedApprover;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestForwarder;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestForwarderRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestForwarderService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestForwarderServiceImpl implements RequestForwarderService {

    private final RequestForwarderRepository requestForwarderRepository;
    private final TenantConfigService tenantConfigService;

    @Override
    public boolean isCurrentForwarder(Long requestId, String toApprovalName) {
        return requestForwarderRepository.getCurrentForwarderByRequestIdAndToApprovalName(requestId, toApprovalName).isPresent();
    }

    @Override
    public boolean usedToForwardApproval(Long requestId) {
        return requestForwarderRepository.usedToForwardApprovalWorkflow(requestId).isPresent();
    }

    @Override
    public ForwardedApprover getForwardedApprover(Long requestId) {
        RequestForwarder requestForwarder = requestForwarderRepository.usedToForwardApprovalWorkflow(requestId)
                .orElse(null);

        if (requestForwarder == null) {
            return null;
        }

        ForwardedApprover forwardedApprover = new ForwardedApprover();
        forwardedApprover.setForwardedApprover(requestForwarder.getToApprover());
        forwardedApprover.setFromApprover(requestForwarder.getFromApprover());
        forwardedApprover.setForwaredDate(requestForwarder.getCreatedDate());
        return forwardedApprover;
    }

    @Override
    public boolean isForwardApprovalWorkflow(Long requestId, String assignBy, String userLogin, Integer tenantId) {
        boolean isCurrentForwarder = isCurrentForwarder(requestId, userLogin);
        boolean usedToForwardApprovalWorkflow = usedToForwardApproval(requestId);
        if(usedToForwardApprovalWorkflow) {
            if (userLogin.equalsIgnoreCase(assignBy) && isCurrentForwarder) {
                return tenantConfigService.getForwardApprovalWorkflow(tenantId);
            } else {
                return false;
            }
        } else {
            return tenantConfigService.getForwardApprovalWorkflow(tenantId);
        }

    }


}
