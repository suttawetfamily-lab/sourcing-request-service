package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RequestAttachmentMapper {

    RequestAttachmentMapper INSTANCE = Mappers.getMapper(RequestAttachmentMapper.class);

    List<RequestAttachmentDto> toRequestAttachmentDtoList(List<RequestAttachment> requestAttachment);

    RequestAttachmentDto toRequestAttachmentDto(RequestAttachment requestAttachment);

}
