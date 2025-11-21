package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemAttachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.ExistingPriceItemAttachmentKey;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemAttachmentRequest;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.AttachmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ExistingPriceItemAttachmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ExistingPriceItemRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ExistingPriceItemAttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExistingPriceItemAttachmentServiceImpl implements ExistingPriceItemAttachmentService {

    private final ExistingPriceItemRepository existingPriceItemRepository;

    private final ExistingPriceItemAttachmentRepository existingPriceItemAttachmentRepository;

    private final AttachmentRepository attachmentRepository;

    @Override
    public ExistingPriceItemAttachment saveExistingPriceItemAttachment(ExistingPriceItemAttachmentRequest existingPriceItemAttachmentRequest) {
        return this.upsertExistingPriceItemAttachment(existingPriceItemAttachmentRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveExistingPriceItemAttachment(ExistingPriceItem existingPriceItem, List<ExistingPriceItemAttachmentDto> existingPriceItemAttachmentRequest) {
        List<ExistingPriceItemAttachment> existingPriceItemAttachments = new ArrayList<>();
        // delete Request Attachment
        existingPriceItemAttachmentRepository.deleteExistingPriceItemAttachmentByExistingPriceItemId(existingPriceItem.getRecId());

        for (ExistingPriceItemAttachmentDto existingPriceItemAttachmentdto : existingPriceItemAttachmentRequest) {
            Attachment attachment = attachmentRepository.findAttachmentByRecId(existingPriceItemAttachmentdto.getAttachment().getRecId())
                    .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));

            ExistingPriceItemAttachmentKey existingPriceItemAttachmentKey = new ExistingPriceItemAttachmentKey();
            existingPriceItemAttachmentKey.setExistingPriceItemId(existingPriceItem.getRecId());
            existingPriceItemAttachmentKey.setAttachmentId(attachment.getRecId());

            ExistingPriceItemAttachment existingPriceItemAttachment = ExistingPriceItemAttachment.builder()
                    .id(existingPriceItemAttachmentKey)
                    .existingPriceItem(existingPriceItem)
                    .attachment(attachment)
                    .lineNum(existingPriceItemAttachmentdto.getLineNum())
                    .note(existingPriceItemAttachmentdto.getNote())
                    .sendtoSupplier(existingPriceItemAttachmentdto.isSendtoSupplier())
                    .build();

            existingPriceItemAttachments.add(existingPriceItemAttachment);
        }

        if (!existingPriceItemAttachments.isEmpty()) {
            existingPriceItemAttachmentRepository.saveAll(existingPriceItemAttachments);
        }
    }

    private ExistingPriceItemAttachment upsertExistingPriceItemAttachment(ExistingPriceItemAttachmentRequest existingPriceItemAttachmentRequest) {
        ExistingPriceItemAttachment existingPriceItemAttachment = setExistingPriceItemAttachment(existingPriceItemAttachmentRequest);
        ExistingPriceItemAttachment existingExistingPriceItemAttachment = existingPriceItemAttachmentRepository.findExistingPriceItemAttachmentByExistingPriceItemAndAttachment(existingPriceItemAttachment.getExistingPriceItem(), existingPriceItemAttachment.getAttachment());

        String note = null;
        if(existingExistingPriceItemAttachment == null) {
            if(existingPriceItemAttachmentRequest.getNote() != null && !existingPriceItemAttachmentRequest.getNote().trim().equals("")){
                note = existingPriceItemAttachmentRequest.getNote().trim();
            }
            existingPriceItemAttachmentRepository.saveExistingPriceItemAttachment(existingPriceItemAttachmentRequest.getExistingPriceItemId(),
                    existingPriceItemAttachmentRequest.getAttachmentId(),
                    existingPriceItemAttachmentRequest.getLineNum(),
                    Boolean.parseBoolean(existingPriceItemAttachmentRequest.getSendToSupplier()),
                    note);
        }
        else {
            if(existingPriceItemAttachmentRequest.getNote() != null && !existingPriceItemAttachmentRequest.getNote().trim().equals("")){
                note = existingPriceItemAttachmentRequest.getNote().trim();
            }
            existingPriceItemAttachmentRepository.updateExistingPriceItemAttachment(existingPriceItemAttachmentRequest.getExistingPriceItemId(),
                    existingPriceItemAttachmentRequest.getAttachmentId(),
                    existingPriceItemAttachmentRequest.getLineNum(),
                    Boolean.parseBoolean(existingPriceItemAttachmentRequest.getSendToSupplier()),
                    note);
        }
        return existingPriceItemAttachment;
    }
    private ExistingPriceItemAttachment setExistingPriceItemAttachment(ExistingPriceItemAttachmentRequest existingPriceItemAttachmentRequest){
        ExistingPriceItem existingPriceItem = existingPriceItemRepository.findExistingPriceItemByRecId(existingPriceItemAttachmentRequest.getExistingPriceItemId());
        Attachment attachment = attachmentRepository.findAttachmentByRecId(existingPriceItemAttachmentRequest.getAttachmentId())
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));

        ExistingPriceItemAttachment existingPriceItemAttachment = new ExistingPriceItemAttachment();

        existingPriceItemAttachment.setExistingPriceItem(existingPriceItem);
        existingPriceItemAttachment.setAttachment(attachment);
        existingPriceItemAttachment.setLineNum(existingPriceItemAttachmentRequest.getLineNum());
        existingPriceItemAttachment.setSendtoSupplier(Boolean.parseBoolean(existingPriceItemAttachmentRequest.getSendToSupplier()));

        return existingPriceItemAttachment;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByExistingPriceItemAndAttachment(Long existingPriceItemId, Long attachmentId) {
        try {
            ExistingPriceItem existingPriceItem = existingPriceItemRepository.findExistingPriceItemByRecId(existingPriceItemId);
            Attachment attachment = attachmentRepository.findAttachmentByRecId(attachmentId)
                    .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));
            existingPriceItemAttachmentRepository.deleteExistingPriceItemAttachmentByExistingPriceItemAndAttachment(existingPriceItem, attachment);
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }

    @Override
    public List<ExistingPriceItemAttachment> saveExistingPriceItemAttachmentList(List<Attachment> attachmentList, Long existingPriceItemId) {
        List<ExistingPriceItemAttachment> existingPriceItemAttachmentList = new ArrayList<>();
        try {
            int lineNum = 1;
            for (Attachment attachment : attachmentList){
                ExistingPriceItemAttachmentRequest attachmentRequest = new ExistingPriceItemAttachmentRequest();
                attachmentRequest.setExistingPriceItemId(existingPriceItemId);
                attachmentRequest.setAttachmentId(attachment.getRecId());
                attachmentRequest.setLineNum(lineNum++);
                attachmentRequest.setSendToSupplier("0");
                attachmentRequest.setNote(null);
                ExistingPriceItemAttachment existingPriceItemAttachment = saveExistingPriceItemAttachment(attachmentRequest);
                existingPriceItemAttachmentList.add(existingPriceItemAttachment);
            }
        } catch (Exception ex) {
            throw new BusinessException(ApiMessage.E7044, ApiMessage.E7044.description());
        }

        return existingPriceItemAttachmentList;
    }

    @Override
    public void deleteByExistingPriceItemId(Long existingPriceItemId) {
        log.info("Delete all RequestItem Attachment by ExistingPriceItem ID : {}", existingPriceItemId);
        existingPriceItemAttachmentRepository.deleteExistingPriceItemAttachmentByExistingPriceItemId(existingPriceItemId);
    }
}
