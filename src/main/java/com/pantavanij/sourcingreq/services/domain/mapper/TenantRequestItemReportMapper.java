package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailFieldNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestItemReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetail;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TenantRequestItemReportMapper {
    TenantRequestItemReportMapper INSTANCE = Mappers.getMapper(TenantRequestItemReportMapper.class);

    @Mapping(target = "requestItemReport", ignore = true)
    @Mapping(target = "tenantSectionDetail", ignore = true)
    @Mapping(target = "templateId", source = "tenantTemplate.recId")
    TenantRequestItemReportDto toTenantRequestItemReportDto(TenantRequestItemReport tenantRequestItemReport);
    List<TenantRequestItemReportDto> toTenantRequestItemReportDtoList(List<TenantRequestItemReport> tenantRequestItemReportList);

    default TenantRequestItemReportDto toTenantRequestItemReportDto(TenantRequestItemReport tenantRequestItemReport, String timeZone) {
        TenantRequestItemReportDto tenantRequestItemReportDto = toTenantRequestItemReportDto(tenantRequestItemReport);
        if (tenantRequestItemReportDto != null) {
            RequestItemReport requestItemReport = tenantRequestItemReport.getRequestItemReport();
            if (tenantRequestItemReportDto.getRequestItemReport() == null) {
                RequestItemReportDto requestItemReportDto = RequestItemReportMapper.INSTANCE.toRequestItemReportDto(requestItemReport);
                tenantRequestItemReportDto.setRequestItemReport(requestItemReportDto);
            }

            TenantSectionDetail tenantSectionDetail = tenantRequestItemReport.getTenantSectionDetail();
            if(tenantRequestItemReportDto.getTenantSectionDetail() == null) {
                TenantSectionDetailFieldNameDto tenantSectionDetailDto = TenantSectionDetailMapper.INSTANCE.toTenantSectionDetailFieldNameDto(tenantSectionDetail);
                tenantRequestItemReportDto.setTenantSectionDetail(tenantSectionDetailDto);
            }

            tenantRequestItemReportDto.setIsDefault(tenantRequestItemReport.isDefault());
            tenantRequestItemReportDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantRequestItemReportDto.getCreatedDate(), timeZone));
            tenantRequestItemReportDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantRequestItemReportDto.getUpdatedDate(), timeZone));

        }
        return tenantRequestItemReportDto;
    }

    default List<TenantRequestItemReportDto> toTenantRequestItemReportDtoList(List<TenantRequestItemReport> tenantRequestItemReportList, String timeZone) {
        List<TenantRequestItemReportDto> tenantRequestItemReportDtoList = new ArrayList<>();
        if (!tenantRequestItemReportList.isEmpty()) {
            tenantRequestItemReportList.forEach(i -> tenantRequestItemReportDtoList.add(toTenantRequestItemReportDto(i, timeZone)));
        }
        return tenantRequestItemReportDtoList;
    }
}
