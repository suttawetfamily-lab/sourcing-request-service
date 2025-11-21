package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestAttachment;
import com.pantavanij.sourcingreq.services.domain.request.RequestAttachmentRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestRequest;

import java.util.List;

public interface RequestAttachmentService {

    RequestAttachment saveRequestAttachment(RequestAttachmentRequest requestAttachmentRequest);

    void saveRequestAttachment(Request request, RequestRequest reqRequest);

    void savePDPARequestAttachment(Request request, RequestRequest reqRequest, String fileId);

    boolean deleteByRequestAndAttachment(Long requestId, Long attachmentId);

    void deleteByRequestId(Request request);

    void deletePDPAByRequestId(Request request);
}
