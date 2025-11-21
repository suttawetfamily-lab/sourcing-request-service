package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingMenuDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingMenu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SourcingMenuMapper {

    SourcingMenuMapper INSTANCE = Mappers.getMapper(SourcingMenuMapper.class);

    List<SourcingMenuDto> toSourcingMenuDtoList(List<SourcingMenu> sourcingMenus);

    @Mapping(source = "sourcingMenu.recId", target = "sourcingMenuId")
    SourcingMenuDto toSourcingMenuDto(SourcingMenu sourcingMenu);

}
