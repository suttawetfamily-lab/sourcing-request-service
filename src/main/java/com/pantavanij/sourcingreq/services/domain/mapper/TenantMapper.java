package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestTenantDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;

@Mapper
public interface TenantMapper {

    TenantMapper INSTANCE = Mappers.getMapper(TenantMapper.class);

    @Mapping(source = "tenant.recId", target = "tenantId")
    @Mapping(source = "tenant.code", target = "tenantCode")
    @Mapping(source = "tenant.name", target = "tenantName")
    @Mapping(source = "tenant.description", target = "tenantDescription")
    TenantDto toTenantDto(Tenant tenant);

    default TenantDto toTenantDto(Tenant tenant, String timeZone) {
        TenantDto tenantDto = toTenantDto(tenant);
        if (tenantDto != null) {
            tenantDto.setCreatedDate(DateTimeUtil.convertTimestampByUserTimeZone(tenantDto.getCreatedDate(), timeZone));
        }
        return tenantDto;
    }

    default List<TenantDto> toTenantDtoList(List<Tenant> tenantList, String timeZone) {
        List<TenantDto> tenantDtoList = new ArrayList<>();
        if (!tenantList.isEmpty()) {
            tenantList.forEach(tenant -> {
                tenantDtoList.add(toTenantDto(tenant, timeZone));
            });
        }
        return tenantDtoList;
    }

    RequestTenantDto mapRequestTenantToDto(Tenant tenant);

}
