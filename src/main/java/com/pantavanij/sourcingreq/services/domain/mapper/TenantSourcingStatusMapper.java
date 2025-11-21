package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.TenantSourcingStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSourcingStatus;
//import com.pantavanij.sourcingreq.services.domain.projection.TenantSourcingStatusOptionProjection;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TenantSourcingStatusMapper {
    TenantSourcingStatusMapper INSTANCE = Mappers.getMapper(TenantSourcingStatusMapper.class);

    TenantSourcingStatusDto toTenantSourcingStatusDto(TenantSourcingStatus tenantSourcingStatus);
    //List<TenantSourcingStatusDto> toTenantSourcingStatusDtoList(List<TenantSourcingStatus> tenantSourcingStatusList);

    default TenantSourcingStatusDto toTenantSourcingStatusDto(TenantSourcingStatus tenantSourcingStatus, String timeZone) {
        TenantSourcingStatusDto tenantSourcingStatusDto = toTenantSourcingStatusDto(tenantSourcingStatus);
        if (tenantSourcingStatusDto != null) {
            tenantSourcingStatusDto.setDefault(tenantSourcingStatus.isDefault());
            tenantSourcingStatusDto.setSourcingStatusObj(SourcingStatusMapper.INSTANCE.sourcingStatusToOptionDto(tenantSourcingStatus.getSourcingStatus()));
            tenantSourcingStatusDto.setSourcingStatusCode(tenantSourcingStatus.getSourcingStatus().getCode());
            tenantSourcingStatusDto.setSourcingStatusDescription(tenantSourcingStatus.getSourcingStatus().getDescription());
            tenantSourcingStatusDto.setRecId((tenantSourcingStatus.getSourcingStatus().getRecId()));
            tenantSourcingStatusDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantSourcingStatusDto.getCreatedDate(), timeZone));
            tenantSourcingStatusDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantSourcingStatusDto.getUpdatedDate(), timeZone));
        }
        return tenantSourcingStatusDto;
    }

    default List<TenantSourcingStatusDto> toTenantSourcingStatusDtoList(List<TenantSourcingStatus> tenantSourcingStatusList) {
        List<TenantSourcingStatusDto> tenantSourcingStatusDtoList = new ArrayList<>();
        if (!tenantSourcingStatusList.isEmpty()) {
            tenantSourcingStatusList.forEach(i -> {
                TenantSourcingStatusDto tenantSourcingStatusDto = toTenantSourcingStatusDto(i);
                tenantSourcingStatusDto.setSourcingStatusObj(SourcingStatusMapper.INSTANCE.sourcingStatusToOptionDto(i.getSourcingStatus()));
                tenantSourcingStatusDto.setSourcingStatusCode(i.getSourcingStatus().getCode());
                tenantSourcingStatusDto.setSourcingStatusDescription(i.getSourcingStatus().getDescription());
                tenantSourcingStatusDto.setRecId((i.getSourcingStatus().getRecId()));
                tenantSourcingStatusDtoList.add(tenantSourcingStatusDto);
            });
        }
        return tenantSourcingStatusDtoList;
    }

    default List<TenantSourcingStatusDto> toTenantSourcingStatusDtoList(List<TenantSourcingStatus> tenantSourcingStatusList, String timeZone) {
        List<TenantSourcingStatusDto> tenantSourcingStatusDtoList = new ArrayList<>();
        if (!tenantSourcingStatusList.isEmpty()) {
            tenantSourcingStatusList.forEach(i -> {
                TenantSourcingStatusDto tenantSourcingStatusDto = toTenantSourcingStatusDto(i, timeZone);
                tenantSourcingStatusDtoList.add(tenantSourcingStatusDto);
            });
        }
        return tenantSourcingStatusDtoList;
    }
//
//    @Mapping(source = "value", target = "value")
//    @Mapping(source = "code", target = "name")
//    @Mapping(source = "name", target = "label")
//    @Mapping(source = "isDefault", target = "isDefault")
//    OptionDto toOptionDto(TenantSourcingStatusOptionProjection tenantSourcingStatus);
//
//    List<OptionDto> toOptionDtoList(List<TenantSourcingStatusOptionProjection> tenantSourcingStatusList);
}
