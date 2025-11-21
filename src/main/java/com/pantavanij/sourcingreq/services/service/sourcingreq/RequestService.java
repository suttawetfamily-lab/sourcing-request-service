package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.ReviewerRequestDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestService {

    RequestDto findRequestSourcingForEditingByRecId(Long recId);

    RequesterRequestDto findRequestSourcingByRecId(Long recId);

    ApproverRequestDto findRequestApprovalByRecId(Long recId);

    ReviewerRequestDto findRequestReviewerByRecId(Long recId);

    RequestDto findByRecIdForDuplicate(Long requestId);

    RequestDto cancelRequest(RequestCancellationRequest cancellationRequest);

    RequestDto approveRequest(RequestApproveRequest approveRequest);
    boolean approveRequest(RequestDeptApprovalRequest deptApprovalRequest);

    boolean forwardApproval(RequestForwarderRequest forwarderRequest);
    void sendEmailForward(RequestForwarderRequest forwarderRequest);
    RequestDto rejectRequest(RequestRejectRequest rejectRequest);
    boolean rejectRequest(RequestDeptApprovalRequest request);
    Request searchRequestByRecId(Long requestId);

    RequesterRequestSearchDto searchRequestByCondition(RequestSearchRequest searchRequest, Pageable pageable);

    ApproverRequestSearchDto searchAllApprovalListByCondition(ApprovalSearchRequest searchRequest, Pageable pageable);

    ApproverRequestSearchDto searchMyApprovalListByCondition(ApprovalSearchRequest searchRequest, Pageable pageable);

    List<RequesterRequestDto> searchRequestExcelByCondition(RequestSearchRequest searchRequest);

    boolean validateRequest(Long requestId);

    RequestDto saveRequest(RequestRequest request, EPAuthReviewerResponse response, boolean isSaveDraft);

    boolean deleteRequestByRecId(Long requestId);

    boolean assignRequestByRecId(Long requestId);

    boolean removeRequestByRecId(Long requestId);

    RequestSearchDto searchRequestReviewerByCondition(RequestSearchRequest searchRequest, Pageable pageable);

    RequestDeptApproverSearchDto searchRequestDeptApproverByCondition(RequestDeptApproverSearchRequest searchRequest, List<Long> requestId, Pageable pageable);

    RequestDto getRequestByRequestNo(String requestNo, Integer tenant);

    DefaultApproverDto getDefaultApprovers();

//    void updateRequestStatusByERFX(Request request);
//
//    void updateRequestStatusByERFX(Request request, List<Long> eRFXDocNums, String refreshToken);
//
//    void updateRequestStatusByERFX(List<Long> eRFXDocNums, List<Request> requestList, String refreshToken);
//
//    void getTopNUpdatedERFXRequest(Tenant tenant, String maxItems, String refreshToken);

    List<DefaultApprovalDto> getDefaultApproval(String erfxid, Double amount);

    InstanceApproverHeaderDto getPurchaserHeaders(String tenantId, String idp, Request request);

    void updateRequestStatusAfterSourcing(Request request);
}
