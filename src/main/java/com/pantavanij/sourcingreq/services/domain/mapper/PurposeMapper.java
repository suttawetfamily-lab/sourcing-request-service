package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.PurposeDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purpose;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PurposeMapper {
    PurposeMapper INSTANCE = Mappers.getMapper(PurposeMapper.class);

    PurposeDto toPurposeDto(Purpose purpose);
    List<PurposeDto> toPurposeDtoList(List<Purpose> purpose);

    @Mapping(target = "value", source = "purpose.recId")
    @Mapping(target = "name", source = "purpose.code")
    @Mapping(target = "label", source = "purpose.name")
    OptionDto toPurposeOptionDto(Purpose purpose);

    List<OptionDto> toPurposeOptionDto(List<Purpose> categories);
}
