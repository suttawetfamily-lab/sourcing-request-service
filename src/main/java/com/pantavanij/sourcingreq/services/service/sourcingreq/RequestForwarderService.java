package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.approver.ForwardedApprover;

public interface RequestForwarderService {


    boolean isCurrentForwarder(Long requestId, String toApprovalName);

    boolean isForwardApprovalWorkflow(Long requestId, String assignBy, String userLogin, Integer tenantId);

    boolean usedToForwardApproval(Long requestId);

    ForwardedApprover getForwardedApprover(Long requestId);
}
