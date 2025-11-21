package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ExistingPriceItemAttachmentMapper {

    ExistingPriceItemAttachmentMapper INSTANCE = Mappers.getMapper(ExistingPriceItemAttachmentMapper.class);

    List<ExistingPriceItemAttachmentDto> toExistingPriceItemAttachmentDtoList(List<ExistingPriceItemAttachment> existingPriceItemAttachment);

    ExistingPriceItemAttachmentDto toExistingPriceItemAttachmentDto(ExistingPriceItemAttachment existingPriceItemAttachment);


}
