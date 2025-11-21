package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RequestItemAttachmentMapper {

    RequestItemAttachmentMapper INSTANCE = Mappers.getMapper(RequestItemAttachmentMapper.class);

    List<RequestItemAttachmentDto> toRequestItemAttachmentDtoList(List<RequestItemAttachment> requestItemAttachment);

    RequestItemAttachmentDto toRequestItemAttachmentDto(RequestItemAttachment requestItemAttachment);

}
