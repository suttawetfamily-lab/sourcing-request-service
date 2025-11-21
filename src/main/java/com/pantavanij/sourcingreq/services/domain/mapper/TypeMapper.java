package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.TypeDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Type;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;

@Mapper
public interface TypeMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    TypeDto toTypeDto(Type type);

    @Mapping(target = "value", source = "type.recId")
    @Mapping(target = "name", source = "type.code")
    @Mapping(target = "label", source = "type.name")
    @Mapping(target = "isDefault", source = "type.default")
    OptionDto toTypeOptionDto(Type type);

    List<OptionDto> toTypeOptionDto(List<Type> types);

    default TypeDto toTypeDto(Type type, String timeZone) {
        TypeDto typeDto = toTypeDto(type);
        if (typeDto != null) {
            OptionDto typeObj = toTypeOptionDto(type);
            typeDto.setTypeObj(typeObj);
            typeDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(typeDto.getCreatedDate(), timeZone));
            typeDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(typeDto.getUpdatedDate(), timeZone));
        }
        return typeDto;
    }

    default List<TypeDto> toTypeDtoList(List<Type> typeList, String timeZone) {
        List<TypeDto> typeDtoList = new ArrayList<>();
        if (!typeList.isEmpty()) {
            typeList.forEach(i -> typeDtoList.add(toTypeDto(i, timeZone)));
        }
        return typeDtoList;
    }


}
