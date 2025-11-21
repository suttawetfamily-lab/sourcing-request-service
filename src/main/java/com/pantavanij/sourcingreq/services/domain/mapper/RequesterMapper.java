package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequesterDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Requester;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RequesterMapper {
    RequesterMapper INSTANCE = Mappers.getMapper(RequesterMapper.class);

    RequesterDto toRequestRequesterDto(Requester requestRequester);

    List<RequesterDto> toRequestRequesterDtoList(List<Requester> requestRequesters);
}
