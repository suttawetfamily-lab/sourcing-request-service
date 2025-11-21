package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.TenantConfigDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantConfig;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TenantConfigMapper {
    TenantConfigMapper INSTANCE = Mappers.getMapper(TenantConfigMapper.class);

    TenantConfigDto toTenantConfigDto(TenantConfig tenantConfig);

    default TenantConfigDto toTenantConfigDto(TenantConfig tenantConfig, String timeZone) {
        TenantConfigDto tenantConfigDto = toTenantConfigDto(tenantConfig);
        if (tenantConfigDto != null) {
            tenantConfigDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantConfigDto.getCreatedDate(), timeZone));
            tenantConfigDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantConfigDto.getUpdatedDate(), timeZone));
        }
        return tenantConfigDto;
    }


    default List<TenantConfigDto> toTenantConfigDtoList(List<TenantConfig> tenantConfigs, String timeZone) {
        List<TenantConfigDto> tenantDtoList = new ArrayList<>();
        if(tenantConfigs != null) {
            tenantConfigs.forEach(i -> tenantDtoList.add(toTenantConfigDto(i, timeZone)));
        }
        return tenantDtoList;
    }
}
