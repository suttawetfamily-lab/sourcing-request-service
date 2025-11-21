package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.BorgUserAdditionalDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrganizationMapper {

    OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

    @Mapping(target = "value", source = "borgUserAdditionalDto.borgID")
    @Mapping(target = "name", source = "borgUserAdditionalDto.borgID")
    @Mapping(target = "label", source = "borgUserAdditionalDto.borgName")
    OptionDto toOrganizationOptionDto(BorgUserAdditionalDto borgUserAdditionalDto);

    List<OptionDto> toOrganizationOptionDto(List<BorgUserAdditionalDto> borgUserAdditionalDtos);

}
