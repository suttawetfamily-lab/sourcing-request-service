package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ReviewerDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Reviewer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RequestGridFieldMapper {
    RequestGridFieldMapper INSTANCE = Mappers.getMapper(RequestGridFieldMapper.class);

}
