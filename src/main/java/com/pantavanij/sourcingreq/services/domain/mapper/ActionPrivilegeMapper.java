package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ActionPrivilegeDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ActionPrivilege;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ActionPrivilegeMapper {

    ActionPrivilegeMapper INSTANCE = Mappers.getMapper(ActionPrivilegeMapper.class);

    List<ActionPrivilegeDto> toActionPrivilegeDto(List<ActionPrivilege> actionPrivilege);

    ActionPrivilegeDto toActionPrivilegeDto(ActionPrivilege actionPrivilege);
}
