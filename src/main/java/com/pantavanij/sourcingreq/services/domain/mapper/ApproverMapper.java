package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ApproverDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Approver;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ApproverMapper {
    ApproverMapper INSTANCE = Mappers.getMapper(ApproverMapper.class);
    ApproverDto toApproverDto(Approver requestApprover);
    List<ApproverDto> toRequestApproverDtoList(List<Approver> requestApprovers);
}
