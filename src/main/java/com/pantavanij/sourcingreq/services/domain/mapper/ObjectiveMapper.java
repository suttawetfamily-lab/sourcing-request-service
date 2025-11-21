package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ObjectiveDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Objective;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ObjectiveMapper {
    ObjectiveMapper INSTANCE = Mappers.getMapper(ObjectiveMapper.class);

    ObjectiveDto toObjectiveDto(Objective objective);

    @Mapping(target = "value", source = "objective.recId")
    @Mapping(target = "name", source = "objective.code")
    @Mapping(target = "label", source = "objective.name")
    OptionDto toObjectiveOptionDto(Objective objective);

    List<OptionDto> toObjectiveOptionDto(List<Objective> categories);
}
