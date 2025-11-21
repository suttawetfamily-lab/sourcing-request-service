package com.pantavanij.sourcingreq.services.domain.mapper;


import com.pantavanij.sourcingreq.services.domain.dto.RequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailFieldNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestReport;
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
public interface RequestReportMapper {
    RequestReportMapper INSTANCE = Mappers.getMapper(RequestReportMapper.class);

    @Mapping(target = "tenantRequestReportList", ignore = true)
    RequestReportDto toRequestReportDto(RequestReport requestRequestReport);

    List<RequestReportDto> toRequestReportDtoList(List<RequestReport> requestRequestReports);

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
        return dto;
    }


    default RequestReportDto toRequestReportDto(RequestReport requestReport, String timeZone) {
        RequestReportDto requestReportDto = toRequestReportDto(requestReport);
        if (requestReportDto != null) {
            requestReportDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestReportDto.getCreatedDate(), timeZone));
            requestReportDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestReportDto.getUpdatedDate(), timeZone));
            if (requestReport.getTenantRequestReportList() != null) {
                List<TenantRequestReportDto> tenantRequestReportDtos = requestReport.getTenantRequestReportList()
                        .stream()
                        .sorted(Comparator.comparingInt(TenantRequestReport::getSequence)) // เรียง tenantRequestReports ตาม sequence
                        .map(tenantRequestReport -> {
                            TenantSectionDetailFieldNameDto tenantSectionDetailDto = tenantRequestReport.getTenantSectionDetail() != null
                                    ? mapTenantSectionDetailToDto(tenantRequestReport.getTenantSectionDetail())
                                    : null;

                            return TenantRequestReportDto.builder()
                                    .recId(tenantRequestReport.getSequence())
                                    .requestReport(null)
                                    .tenantSectionDetail(tenantSectionDetailDto)
                                    .active(tenantRequestReport.isActive())
                                    .isDefault(tenantRequestReport.isDefault())
                                    .sequence(tenantRequestReport.getSequence())
                                    .createdDate(DateTimeUtil.convertTimestampByUserTimeZone(
                                            tenantRequestReport.getCreatedDate(), timeZone))
                                    .createdBy(tenantRequestReport.getCreatedBy())
                                    .updatedDate(DateTimeUtil.convertTimestampByUserTimeZone(
                                            tenantRequestReport.getUpdatedDate(), timeZone))
                                    .updatedBy(tenantRequestReport.getUpdatedBy())
                                    .build();
                        })
                        .collect(Collectors.toList());

                requestReportDto.setTenantRequestReportList(tenantRequestReportDtos);
            }
        }
        return requestReportDto;
    }


    default List<RequestReportDto> toRequestReportDtoList(List<RequestReport> requestReportList, String timeZone) {
        List<RequestReportDto> requestReportDtoList = new ArrayList();
        if (requestReportList != null) {
            requestReportList.forEach(i -> requestReportDtoList.add(toRequestReportDto(i, timeZone)));
        }
        return requestReportDtoList;
    }
}
