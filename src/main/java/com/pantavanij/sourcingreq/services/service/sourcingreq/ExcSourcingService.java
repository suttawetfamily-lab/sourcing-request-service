package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.DeptApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.DeptApprovalStatusMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.DEPT_APPROVAL_CANCELLED;
import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.DEPT_APPROVAL_NONE;

public interface ExcSourcingService {

    ExcSourcingApproverSearchDto searchExcSourcingApproverByCondition(ExcSourcingApproverSearchRequest searchRequest, Pageable pageable);
    ExcSourcingReponseDto saveExcSourcing(ExcSourcingRequestDto excSourcingRequestDto);
    ExcSourcingDto findByDocNo(String excSourcingDocNo);
    boolean approveExcSourcingApprover(ExcSourcingApprovalRequest deptApprovalRequest);
    boolean rejectExcSourcingApprover(ExcSourcingApprovalRequest deptApprovalRequest);
    boolean approveExcSourcingPurchaser(ExcSourcingApprovalRequest purchaserApprovalRequest);
    boolean rejectExcSourcingPurchaser(ExcSourcingApprovalRequest purchaserApprovalRequest);
    boolean deleteExcSourcingByDocNo(String excSourcingDocNo);
    public List<DeptApprovalStatusDto> getExcSourcingApprovalStatusSearchList();
    public List<TenantExcSourcingStatusDto> getExcSourcingStatusSearchList();
}
