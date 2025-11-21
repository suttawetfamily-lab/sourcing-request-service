package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ApproverDto;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthReviewerDto;
import com.pantavanij.sourcingreq.services.domain.dto.ReportLineDto;
import com.pantavanij.sourcingreq.services.domain.dto.approver.*;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ReportLine;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.ApproverResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthDeptApproverResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthReviewerResponse;
import com.pantavanij.sourcingreq.services.domain.response.ReportLineResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApproverService {
    ApproverResponse getRequestApprover(ApproverSearchRequest request, Pageable pageable);

    Integer saveApprover(ApproverDto approverDto);

    EPAuthDeptApproverResponse getApproverListByConditions(ApproverSearchRequest request);

    ApproverSearchDto searchApproverListByCondition(ApproverSearchRequest request, Pageable pageable);

    ApproverDto findApproverByRecId(Integer approverId);

    ApproverDto createApprover(ApproverRequest approverRequest);

    ApproverDto updateApprover(ApproverRequest approverRequest);

    boolean deleteApprover(Integer approverId);

    ApproverDto updateApproverSequence(SequenceRequest request);
}
