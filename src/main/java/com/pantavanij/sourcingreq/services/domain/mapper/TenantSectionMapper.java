package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailDescriptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSection;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetail;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetailDescription;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.*;

@Mapper
public interface TenantSectionMapper {

    TenantSectionMapper INSTANCE = Mappers.getMapper(TenantSectionMapper.class);

    @Mapping(source = "visible", target = "visible")
    TenantSectionDto toTenantSectionDto(TenantSection tenantSection);

    List<TenantSectionDto> toTenantSectionDto(List<TenantSection> tenantSectionList);

    @Mapping(source = "tenantSectionDetailDescription", target = "description")
    TenantSectionDetailDto toTenantSectionDetailDto(TenantSectionDetail tenantSectionDetail);

    List<TenantSectionDetailDto> toTenantSectionDetailDtoList(List<TenantSectionDetail> tenantSectionDetailList);

    TenantSectionDetailDescriptionDto toTenantSectionDetailDescriptionDto(TenantSectionDetailDescription tenantSectionDetailDescription);

    default List<TenantSectionDto> toTenantSectionDtoList(List<TenantSection> tenantSectionList, String timeZone) {
        List<TenantSectionDto> tenantSectionDtoList = toTenantSectionDto(tenantSectionList);
        if (!tenantSectionDtoList.isEmpty()) {
            tenantSectionDtoList = tenantSectionDtoList.stream()
                    .peek(tenantSectionDto -> {
                        tenantSectionDto.setCreatedDate(
                                DateTimeUtil.convertTimestampByUserTimeZone(tenantSectionDto.getCreatedDate(), timeZone));
                        tenantSectionDto.setUpdatedDate(
                                DateTimeUtil.convertTimestampByUserTimeZone(tenantSectionDto.getUpdatedDate(), timeZone));
                    })
                    .collect(Collectors.toList());
        }
        return tenantSectionDtoList;
    }

    default TenantSectionDto toTenantSectionDto(TenantSection tenantSection, List<TenantSectionDetail> tenantSectionDetailList, String timeZone) {
        TenantSectionDto tenantSectionDto = toTenantSectionDto(tenantSection);
        if (tenantSectionDto != null) {
            tenantSectionDto.setFields(null);
            tenantSectionDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantSectionDto.getCreatedDate(), timeZone));
            tenantSectionDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantSectionDto.getUpdatedDate(), timeZone));

            if (tenantSection.getTenantSectionDetailList() != null && !tenantSection.getTenantSectionDetailList().isEmpty()) {
                tenantSectionDto.setTenantSectionDetailList(
                        TenantSectionDetailMapper.INSTANCE.toTenantSectionDetailDtoList(tenantSectionDto.getTenantSectionDetailList(), tenantSectionDetailList, timeZone));
            }
        }
        return tenantSectionDto;
    }

}
