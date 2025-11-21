package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemAttachment;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemAttachmentRequest;

import java.util.List;

public interface ExistingPriceItemAttachmentService {

    ExistingPriceItemAttachment saveExistingPriceItemAttachment(ExistingPriceItemAttachmentRequest existingPriceItemAttachmentRequest);

    void saveExistingPriceItemAttachment(ExistingPriceItem existingPriceItem, List<ExistingPriceItemAttachmentDto> existingPriceItemAttachmentRequest);

    boolean deleteByExistingPriceItemAndAttachment(Long existingPriceItemId, Long attachmentId);

    List<ExistingPriceItemAttachment> saveExistingPriceItemAttachmentList(List<Attachment> attachmentList, Long existingPriceItemId);

    void deleteByExistingPriceItemId(Long existingPriceItemId);
}
