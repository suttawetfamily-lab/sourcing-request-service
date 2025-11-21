package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemLocationDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemLocation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RequestItemLocationMapper {

    RequestItemLocationMapper INSTANCE = Mappers.getMapper(RequestItemLocationMapper.class);

    List<RequestItemLocationDto> toRequestItemLocationDtoList(List<RequestItemLocation> requestItemLocation);

    RequestItemLocationDto toRequestItemLocationDto(RequestItemLocation requestItemLocation);

}
