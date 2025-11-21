package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestAttachmentKey;
import com.pantavanij.sourcingreq.services.domain.request.DelegationActiveRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestAttachmentRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestRequest;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_DELETE;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestAttachmentServiceImpl implements RequestAttachmentService {

    private final RequestRepository requestRepository;
    private final RequestAttachmentRepository requestAttachmentRepository;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final AttachmentRepository attachmentRepository;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final DelegationService delegationService;
    private final TenantConfigService tenantConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RequestAttachment saveRequestAttachment(RequestAttachmentRequest requestAttachmentRequest) {
        return this.upsertRequestAttachment(requestAttachmentRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRequestAttachment(Request request, RequestRequest reqRequest) {
        List<RequestAttachment> requestAttachments = new ArrayList<>();
        // delete Request Attachment
        requestAttachmentRepository.deleteRequestAttachmentByRequestId(request.getRecId());

        for (RequestAttachmentDto requestAttachmentdto : reqRequest.getRequestAttachmentList()) {
            Attachment attachment = attachmentRepository.findAttachmentByRecId(requestAttachmentdto.getAttachment().getRecId())
                    .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));

            if(!attachment.getFileGroup().equalsIgnoreCase("PDPA") &&
                    !attachment.getFileGroup().equalsIgnoreCase("SYSTEM")) {
                RequestAttachmentKey requestAttachmentKey = new RequestAttachmentKey();
                requestAttachmentKey.setRequestId(request.getRecId());
                requestAttachmentKey.setAttachmentId(attachment.getRecId());

                RequestAttachment requestAttachment = RequestAttachment.builder()
                        .id(requestAttachmentKey)
                        .request(request)
                        .attachment(attachment)
                        .lineNum(requestAttachmentdto.getLineNum())
                        .note(requestAttachmentdto.getNote())
                        .sendtoSupplier(requestAttachmentdto.isSendtoSupplier())
                        .build();

                requestAttachments.add(requestAttachment);
                requestAttachmentRepository.saveRequestAttachment(
                        request.getRecId(),
                        attachment.getRecId(),
                        requestAttachmentdto.getLineNum(),
                        requestAttachmentdto.isSendtoSupplier(),
                        requestAttachmentdto.getNote()
                );
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePDPARequestAttachment(Request request, RequestRequest reqRequest, String fileId) {
        log.info("Add all PDPA Attachment by Request ID : {}", request.getRecId());
        // Add new Due Diligence Checklist Form (TH/ENG) file
        Attachment dueDiligenceCheckListForm = attachmentRepository.findAttachmentByFileId(fileId);
        if(dueDiligenceCheckListForm != null) {
            RequestAttachment requestAttachment = requestAttachmentRepository.findRequestAttachmentByRequestIdAndAttachmentId(request.getRecId(), dueDiligenceCheckListForm.getRecId());
            if(requestAttachment == null) {
                requestAttachmentRepository.saveRequestAttachment(
                        request.getRecId(),
                        dueDiligenceCheckListForm.getRecId(),
                        -1,
                        false,
                        null
                );
            }
        }
    }

    private RequestAttachment upsertRequestAttachment(RequestAttachmentRequest requestAttachmentRequest) {
        RequestAttachment requestAttachment = setRequestAttachment(requestAttachmentRequest);
        RequestAttachment existingRequestAttachment = requestAttachmentRepository.findRequestAttachmentByRequestAndAttachment(requestAttachment.getRequest(), requestAttachment.getAttachment());

        String note = null;
        if(existingRequestAttachment == null) {
            if(requestAttachmentRequest.getNote() != null && !requestAttachmentRequest.getNote().trim().equals("")){
                note = requestAttachmentRequest.getNote().trim();
            }
            requestAttachmentRepository.saveRequestAttachment(requestAttachmentRequest.getRequestId(),
                    requestAttachmentRequest.getAttachmentId(),
                    requestAttachmentRequest.getLineNum(),
                    Boolean.parseBoolean(requestAttachmentRequest.getSendToSupplier()),
                    note);
        }
        else {
            if(requestAttachmentRequest.getNote() != null && !requestAttachmentRequest.getNote().trim().equals("")){
                note = requestAttachmentRequest.getNote().trim();
            }
            requestAttachmentRepository.updateRequestAttachment(requestAttachmentRequest.getRequestId(),
                    requestAttachmentRequest.getAttachmentId(),
                    requestAttachmentRequest.getLineNum(),
                    Boolean.parseBoolean(requestAttachmentRequest.getSendToSupplier()),
                    note);
        }
        return requestAttachment;
    }

    private RequestAttachment setRequestAttachment(RequestAttachmentRequest requestAttachmentRequest){
        Request request = requestRepository.findRequestByRecId(requestAttachmentRequest.getRequestId());
        Attachment attachment = attachmentRepository.findAttachmentByRecId(requestAttachmentRequest.getAttachmentId())
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));

        RequestAttachment requestItemAttachment = new RequestAttachment();

        requestItemAttachment.setRequest(request);
        requestItemAttachment.setAttachment(attachment);
        requestItemAttachment.setLineNum(requestAttachmentRequest.getLineNum());
        requestItemAttachment.setSendtoSupplier(Boolean.parseBoolean(requestAttachmentRequest.getSendToSupplier()));

        return requestItemAttachment;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRequestAndAttachment(Long requestId, Long attachmentId) {
        UserDto user = AppUtil.getUser();
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        if(AppUtil.isAllowedAction(user, request, requestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_DELETE)) {
            try {
                Attachment attachment = attachmentRepository.findAttachmentByRecId(attachmentId)
                        .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));
                requestAttachmentRepository.deleteRequestAttachmentByRequestAndAttachment(request, attachment);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRequestId(Request request) {
        log.info("Delete all Request Attachment by Request ID : {}", request.getRecId());
        requestAttachmentRepository.deleteRequestAttachmentByRequestId(request.getRecId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePDPAByRequestId(Request request) {
        log.info("Delete all PDPA Attachment by Request ID : {}", request.getRecId());
        requestAttachmentRepository.deletePDPARequestAttachmentByRequestId(request.getRecId());
    }
}
