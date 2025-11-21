package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemAttachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemAttachmentKey;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemAttachmentRequest;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.AttachmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemAttachmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemAttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestItemAttachmentServiceImpl implements RequestItemAttachmentService {

    private final RequestItemRepository requestItemRepository;

    private final RequestItemAttachmentRepository requestItemAttachmentRepository;

    private final AttachmentRepository attachmentRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RequestItemAttachment saveRequestItemAttachment(RequestItemAttachmentRequest requestItemAttachmentRequest) {
        return this.upsertRequestItemAttachment(requestItemAttachmentRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRequestItemAttachment(RequestItem requestItem, List<RequestItemAttachmentDto> requestItemAttachmentRequest) {
        List<RequestItemAttachment> requestItemAttachments = new ArrayList<>();
        // delete Request Attachment
        requestItemAttachmentRepository.deleteRequestItemAttachmentByRequestItemId(requestItem.getRecId());

        for (RequestItemAttachmentDto requestItemAttachmentdto : requestItemAttachmentRequest) {
            Attachment attachment = attachmentRepository.findAttachmentByRecId(requestItemAttachmentdto.getAttachment().getRecId())
                    .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));

            RequestItemAttachmentKey requestItemAttachmentKey = new RequestItemAttachmentKey();
            requestItemAttachmentKey.setRequestItemId(requestItem.getRecId());
            requestItemAttachmentKey.setAttachmentId(attachment.getRecId());

            RequestItemAttachment requestItemAttachment = RequestItemAttachment.builder()
                    .id(requestItemAttachmentKey)
                    .requestItem(requestItem)
                    .attachment(attachment)
                    .lineNum(requestItemAttachmentdto.getLineNum())
                    .note(requestItemAttachmentdto.getNote())
                    .sendtoSupplier(requestItemAttachmentdto.isSendtoSupplier())
                    .build();

            requestItemAttachments.add(requestItemAttachment);
        }

        if (!requestItemAttachments.isEmpty()) {
            requestItemAttachmentRepository.saveAll(requestItemAttachments);
        }
    }

    private RequestItemAttachment upsertRequestItemAttachment(RequestItemAttachmentRequest requestItemAttachmentRequest) {
        RequestItemAttachment requestItemAttachment = setRequestItemAttachment(requestItemAttachmentRequest);
        RequestItemAttachment existingRequestItemAttachment = requestItemAttachmentRepository.findRequestItemAttachmentByRequestItemAndAttachment(requestItemAttachment.getRequestItem(), requestItemAttachment.getAttachment());

        String note = null;
        if (existingRequestItemAttachment == null) {
            if (requestItemAttachmentRequest.getNote() != null && !requestItemAttachmentRequest.getNote().trim().equals("")) {
                note = requestItemAttachmentRequest.getNote().trim();
            }
            requestItemAttachmentRepository.saveRequestItemAttachment(requestItemAttachmentRequest.getRequestItemId(),
                    requestItemAttachmentRequest.getAttachmentId(),
                    requestItemAttachmentRequest.getLineNum(),
                    Boolean.parseBoolean(requestItemAttachmentRequest.getSendToSupplier()),
                    note);
        } else {
            if (requestItemAttachmentRequest.getNote() != null && !requestItemAttachmentRequest.getNote().trim().equals("")) {
                note = requestItemAttachmentRequest.getNote().trim();
            }
            requestItemAttachmentRepository.updateRequestItemAttachment(requestItemAttachmentRequest.getRequestItemId(),
                    requestItemAttachmentRequest.getAttachmentId(),
                    requestItemAttachmentRequest.getLineNum(),
                    Boolean.parseBoolean(requestItemAttachmentRequest.getSendToSupplier()),
                    note);
        }
        return requestItemAttachment;
    }
    private RequestItemAttachment setRequestItemAttachment(RequestItemAttachmentRequest requestItemAttachmentRequest) {
        RequestItem requestItem = requestItemRepository.findRequestItemByRecId(requestItemAttachmentRequest.getRequestItemId());
        Attachment attachment = attachmentRepository.findAttachmentByRecId(requestItemAttachmentRequest.getAttachmentId())
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));

        RequestItemAttachment requestItemAttachment = new RequestItemAttachment();

        requestItemAttachment.setRequestItem(requestItem);
        requestItemAttachment.setAttachment(attachment);
        requestItemAttachment.setLineNum(requestItemAttachmentRequest.getLineNum());
        requestItemAttachment.setSendtoSupplier(Boolean.parseBoolean(requestItemAttachmentRequest.getSendToSupplier()));

        return requestItemAttachment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRequestItemAndAttachment(Long requestItemId, Long attachmentId) {
        try {
            requestItemAttachmentRepository.deleteRequestItemAttachmentByRequestItemIdAndAttachmentId(requestItemId, attachmentId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public List<RequestItemAttachment> saveRequestItemAttachmentList(List<Attachment> attachmentList, Long requestItemId) {
        List<RequestItemAttachment> requestItemAttachmentList = new ArrayList<>();

        try {
            int lineNum = 1;
            for (Attachment attachment : attachmentList) {
                RequestItemAttachmentRequest itemAttachmentRequest = new RequestItemAttachmentRequest();
                itemAttachmentRequest.setRequestItemId(requestItemId);
                itemAttachmentRequest.setAttachmentId(attachment.getRecId());
                itemAttachmentRequest.setLineNum(lineNum++);
                itemAttachmentRequest.setSendToSupplier("0");
                itemAttachmentRequest.setNote(null);
                RequestItemAttachment requestItemAttachment = saveRequestItemAttachment(itemAttachmentRequest);
                requestItemAttachmentList.add(requestItemAttachment);
            }
        } catch (Exception ex) {
            throw new BusinessException(ApiMessage.E7045, ApiMessage.E7045.description());
        }

        return requestItemAttachmentList;
    }

    @Override
    public void deleteByRequestItemId(Long requestItemId) {
        log.info("Delete all RequestItem Attachment by RequestItem ID : {}", requestItemId);
        requestItemAttachmentRepository.deleteRequestItemAttachmentByRequestItemId(requestItemId);
    }
}
