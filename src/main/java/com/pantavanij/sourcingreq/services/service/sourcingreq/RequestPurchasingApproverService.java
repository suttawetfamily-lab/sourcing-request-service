package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestDeptApproverDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestApprover;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.DeptApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthDeptApproverResponse;

import java.util.List;

public interface RequestPurchasingApproverService {

    EPAuthDeptApproverResponse getPurchasingApproverListByConditions(DeptApproverSearchRequest request);


}
