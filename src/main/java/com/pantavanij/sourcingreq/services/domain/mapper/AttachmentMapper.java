package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.AttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttachmentMapper {

    AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);

    AttachmentDto toAttachmentDto(Attachment attachment);

    default AttachmentDto toAttachmentDto(Attachment attachment, String timeZone) {
        AttachmentDto attachmentDto = toAttachmentDto(attachment);
        if (attachmentDto != null) {
            attachmentDto.setCreatedDate(DateTimeUtil.convertTimestampByUserTimeZone(attachmentDto.getCreatedDate(), timeZone));
        }
        return attachmentDto;
    }
}
