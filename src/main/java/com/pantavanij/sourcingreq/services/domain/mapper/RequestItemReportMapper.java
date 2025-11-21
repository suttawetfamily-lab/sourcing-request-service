package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.MenuPrivilege;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestItemReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetail;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface RequestItemReportMapper {
    RequestItemReportMapper INSTANCE = Mappers.getMapper(RequestItemReportMapper.class);

    @Mapping(target = "tenantRequestItemReportList", ignore = true)
    @Mapping(target = "menuPrivilege", ignore = true)
    @Mapping(target = "templateId", source = "tenantTemplate.recId")
    RequestItemReportDto toRequestItemReportDto(RequestItemReport requestRequestItemReport);

    List<RequestItemReportDto> toRequestItemReportDtoList(List<RequestItemReport> requestRequestItemReports);

    default TenantSectionDetailFieldNameDto mapTenantSectionDetailToDto(TenantSectionDetail tenantSectionDetail) {
        if (tenantSectionDetail == null) {
            return null;
        }
        TenantSectionDetailFieldNameDto dto = new TenantSectionDetailFieldNameDto();
        dto.setId(tenantSectionDetail.getId());
        dto.setName(tenantSectionDetail.getFieldName());
        dto.setFieldName(tenantSectionDetail.getFieldName());
        dto.setValue(tenantSectionDetail.getId());
        dto.setLabel(tenantSectionDetail.getLabel());
        dto.setReportLabel(tenantSectionDetail.getReportLabel());
        return dto;
    }

    default MenuPrivilegeObjDto mapMenuPrivilegeToDto(MenuPrivilege menuPrivilege) {
        if (menuPrivilege == null) {
            return null;
        }
        MenuPrivilegeObjDto dto = new MenuPrivilegeObjDto();
        dto.setId(menuPrivilege.getRecId());
        dto.setName(menuPrivilege.getPathUrl());
        dto.setLabel(menuPrivilege.getLabel());
        dto.setValue(menuPrivilege.getRecId());
        return dto;
    }

    default RequestItemReportDto toRequestItemReportDto(RequestItemReport requestItemReport, String timeZone) {
        RequestItemReportDto requestItemReportDto = toRequestItemReportDto(requestItemReport);
        if (requestItemReportDto != null) {
            requestItemReportDto.setMenuPrivilege(mapMenuPrivilegeToDto(requestItemReport.getMenuPrivilege()));
            requestItemReportDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestItemReportDto.getCreatedDate(), timeZone));
            requestItemReportDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestItemReportDto.getUpdatedDate(), timeZone));
            if (requestItemReport.getTenantRequestItemReportList() != null) {
                List<TenantRequestItemReportDto> tenantRequestItemReportDtos = requestItemReport.getTenantRequestItemReportList()
                        .stream()
                        .sorted(Comparator.comparingInt(TenantRequestItemReport::getSequence)) // เรียง tenantRequestItemReports ตาม sequence
                        .map(tenantRequestItemReport -> {
                            TenantSectionDetailFieldNameDto tenantSectionDetailDto = tenantRequestItemReport.getTenantSectionDetail() != null
                                    ? mapTenantSectionDetailToDto(tenantRequestItemReport.getTenantSectionDetail())
                                    : null;

                            return TenantRequestItemReportDto.builder()
                                    .recId(tenantRequestItemReport.getSequence())
                                    .requestItemReport(null)
                                    .tenantSectionDetail(tenantSectionDetailDto)
                                    .active(tenantRequestItemReport.isActive())
                                    .isDefault(tenantRequestItemReport.isDefault())
                                    .sequence(tenantRequestItemReport.getSequence())
                                    .createdDate(DateTimeUtil.convertTimestampByUserTimeZone(
                                            tenantRequestItemReport.getCreatedDate(), timeZone))
                                    .createdBy(tenantRequestItemReport.getCreatedBy())
                                    .updatedDate(DateTimeUtil.convertTimestampByUserTimeZone(
                                            tenantRequestItemReport.getUpdatedDate(), timeZone))
                                    .updatedBy(tenantRequestItemReport.getUpdatedBy())
                                    .build();
                        })
                        .collect(Collectors.toList());

                requestItemReportDto.setTenantRequestItemReportList(tenantRequestItemReportDtos);
            }
        }
        return requestItemReportDto;
    }


    default List<RequestItemReportDto> toRequestItemReportDtoList(List<RequestItemReport> requestItemReportList, String timeZone) {
        List<RequestItemReportDto> requestItemReportDtoList = new ArrayList();
        if (requestItemReportList != null) {
            requestItemReportList.forEach(i -> requestItemReportDtoList.add(toRequestItemReportDto(i, timeZone)));
        }
        return requestItemReportDtoList;
    }
}
