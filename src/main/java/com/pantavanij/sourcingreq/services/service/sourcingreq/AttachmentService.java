package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.AttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.ERFXAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.UoloadAttachmentRequest;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface AttachmentService {

    AttachmentDto UploadAttachment(Tenant tenant, UoloadAttachmentRequest request);
    Attachment saveAttachment(Attachment attachment);
    ByteArrayResource downloadAttachment(Tenant tenant, String fileUniqueName);
    boolean deleteAttachment(Tenant tenant, Long attachmentId);
    void deleteUnusedAttachment(Tenant tenant);
    List<Attachment> saveAttachmentERFX(List<ERFXAttachmentDto> attachmentDtoList, Integer tenantId);
}
