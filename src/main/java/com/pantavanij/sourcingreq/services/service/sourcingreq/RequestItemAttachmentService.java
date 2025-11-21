package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemAttachmentRequest;

import java.util.List;

public interface RequestItemAttachmentService {

    RequestItemAttachment saveRequestItemAttachment(RequestItemAttachmentRequest requestItemAttachmentRequest);

    void saveRequestItemAttachment(RequestItem requestItem, List<RequestItemAttachmentDto> requestItemAttachmentRequest);

    boolean deleteByRequestItemAndAttachment(Long requesItemId, Long attachmentId);

    List<RequestItemAttachment> saveRequestItemAttachmentList(List<Attachment> attachmentList, Long requestItemId);

    void deleteByRequestItemId(Long requestItemId);

}
