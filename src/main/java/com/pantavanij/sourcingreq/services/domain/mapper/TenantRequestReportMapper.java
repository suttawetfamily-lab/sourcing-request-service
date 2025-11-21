package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailFieldNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetail;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TenantRequestReportMapper {
    TenantRequestReportMapper INSTANCE = Mappers.getMapper(TenantRequestReportMapper.class);

    @Mapping(target = "requestReport", ignore = true)
    @Mapping(target = "tenantSectionDetail", ignore = true)
    TenantRequestReportDto toTenantRequestReportDto(TenantRequestReport tenantRequestReport);
    List<TenantRequestReportDto> toTenantRequestReportDtoList(List<TenantRequestReport> tenantRequestReportList);

    default TenantRequestReportDto toTenantRequestReportDto(TenantRequestReport tenantRequestReport, String timeZone) {
        TenantRequestReportDto tenantRequestReportDto = toTenantRequestReportDto(tenantRequestReport);
        if (tenantRequestReportDto != null) {
            RequestReport requestReport = tenantRequestReport.getRequestReport();
            if (tenantRequestReportDto.getRequestReport() == null) {
                RequestReportDto requestReportDto = RequestReportMapper.INSTANCE.toRequestReportDto(requestReport);
                tenantRequestReportDto.setRequestReport(requestReportDto);
            }

            TenantSectionDetail tenantSectionDetail = tenantRequestReport.getTenantSectionDetail();
            if(tenantRequestReportDto.getTenantSectionDetail() == null) {
                TenantSectionDetailFieldNameDto tenantSectionDetailDto = TenantSectionDetailMapper.INSTANCE.toTenantSectionDetailFieldNameDto(tenantSectionDetail);
                tenantRequestReportDto.setTenantSectionDetail(tenantSectionDetailDto);
            }

            tenantRequestReportDto.setIsDefault(tenantRequestReport.isDefault());
            tenantRequestReportDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantRequestReportDto.getCreatedDate(), timeZone));
            tenantRequestReportDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantRequestReportDto.getUpdatedDate(), timeZone));

        }
        return tenantRequestReportDto;
    }

    default List<TenantRequestReportDto> toTenantRequestReportDtoList(List<TenantRequestReport> tenantRequestReportList, String timeZone) {
        List<TenantRequestReportDto> tenantRequestReportDtoList = new ArrayList<>();
        if (!tenantRequestReportList.isEmpty()) {
            tenantRequestReportList.forEach(i -> tenantRequestReportDtoList.add(toTenantRequestReportDto(i, timeZone)));
        }
        return tenantRequestReportDtoList;
    }
}
