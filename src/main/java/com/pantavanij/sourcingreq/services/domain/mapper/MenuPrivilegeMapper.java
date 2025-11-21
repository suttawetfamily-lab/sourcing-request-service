package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.MenuPrivilege;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemReport;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.apache.commons.lang.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.*;
import org.mapstruct.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.*;

@Mapper
public interface MenuPrivilegeMapper {

    MenuPrivilegeMapper INSTANCE = Mappers.getMapper(MenuPrivilegeMapper.class);


    List<MenuPrivilegeDto> toMenuPrivilegeDtoList(List<MenuPrivilege> menuPrivilege);

    @Mapping(target = "menuName", source = "menuName")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "updatedDate", source = "updatedDate")
    MenuPrivilegeDto toMenuPrivilegeDto(MenuPrivilege menuPrivilege);

    default List<OptionDto> toOptionDtoList(List<MenuPrivilege> menuPrivilegeList) {
        List<OptionDto> optionDtoList = new ArrayList<>();
        if (menuPrivilegeList != null && !menuPrivilegeList.isEmpty()) {
            optionDtoList = menuPrivilegeList.stream()
                    .map(menu -> {
                        OptionDto optionDto = new OptionDto();
                        List<String> privilegeCodes = Arrays.asList(menu.getPrivilegeCode().split(","));
                        privilegeCodes.forEach(privilegeCode -> {
                            optionDto.setValue(privilegeCode);
                            optionDto.setName(privilegeCode);
                            optionDto.setLabel(privilegeCode.toUpperCase());
                        });
                        return optionDto;
                    })
                    .collect(Collectors.toList());
        }
        return optionDtoList;
    }

    default MenuPrivilegeDto toRequestItemReportDto(MenuPrivilege menuPrivilege, List<RequestItemReport> requestItemReportList) {
        MenuPrivilegeDto menuPrivilegeDto = toMenuPrivilegeDto(menuPrivilege);
        if (menuPrivilegeDto != null) {
            Optional<RequestItemReport> optionalRequestItemReport = requestItemReportList.stream().filter(i -> i.getMenuPrivilege().getRecId() == menuPrivilege.getRecId()).findFirst();
            optionalRequestItemReport.ifPresent(requestItemReport -> menuPrivilegeDto.setRequestItemReport(RequestItemReportMapper.INSTANCE.toRequestItemReportDto(requestItemReport)));
        }
        return menuPrivilegeDto;
    }

    default List<MenuPrivilegeDto> toMenuPrivilegeDtoList(List<MenuPrivilege> menuPrivilegeList, List<RequestItemReport> requestItemReportList) {
        List<MenuPrivilegeDto> menuPrivilegeDtoList = new ArrayList<>();
        if (menuPrivilegeList != null) {
            menuPrivilegeList.forEach(i -> menuPrivilegeDtoList.add(toRequestItemReportDto(i, requestItemReportList)));
        }
        return menuPrivilegeDtoList;
    }

//    default List<MenuPrivilegeDto> toMenuPrivilegeDtoList(List<MenuPrivilege> menuPrivilegeList) {
//        List<MenuPrivilegeDto> menuPrivilegeDtoList = new ArrayList<>();
//        if (menuPrivilegeList != null) {
//            menuPrivilegeList.forEach(i -> menuPrivilegeDtoList.add(toMenuPrivilegeDto(i)));
//        }
//        return menuPrivilegeDtoList;
//    }

    default List<MenuPrivilegeObjDto> toMenuPrivilegeDynamicDtoList(List<MenuPrivilege> menuPrivileges) {
        List<Integer> menuPrivilegeList = new ArrayList<>();
        List<MenuPrivilegeObjDto> menuPrivilegeDtoList = new ArrayList<>();
        for (MenuPrivilege menuPrivilege : menuPrivileges) {
            if (!menuPrivilegeList.contains(menuPrivilege.getRecId())) {
                String label = menuPrivilege.getLabel();
                MenuPrivilegeObjDto dto = new MenuPrivilegeObjDto();
                dto.setId(menuPrivilege.getRecId());
                dto.setName(menuPrivilege.getPathUrl());
                dto.setValue(menuPrivilege.getRecId());
                dto.setLabel(label);
                menuPrivilegeDtoList.add(dto);
            }
            menuPrivilegeList.add(menuPrivilege.getRecId());
        }
        return menuPrivilegeDtoList;
    }

}
