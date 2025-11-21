package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.projection.TenantUnitOptionProjection;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

import java.util.*;

@Mapper
public interface TenantUnitMapper {
    TenantUnitMapper INSTANCE = Mappers.getMapper(TenantUnitMapper.class);
    TenantUnitDto toTenantUnitDto(TenantUnit tenantUnit);

    default TenantUnitDto toTenantUnitDto(TenantUnit tenantUnit, String timeZone) {
        TenantUnitDto tenantUnitDto = toTenantUnitDto(tenantUnit);
        if (tenantUnitDto != null) {
            Unit unit = tenantUnit.getUnit();
            OptionDto optionDto = UnitMapper.INSTANCE.toUnitOptionDto(unit);
            tenantUnitDto.setUnitObj(optionDto);
            tenantUnitDto.setDefault(tenantUnit.isDefault());
            tenantUnitDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantUnitDto.getCreatedDate(), timeZone));
            tenantUnitDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantUnitDto.getUpdatedDate(), timeZone));
        }
        return tenantUnitDto;
    }

    default List<TenantUnitDto> toTenantUnitList(List<TenantUnit> tenantUnitList, String timeZone) {
        List<TenantUnitDto> tennatUnitDtoList = new ArrayList<>();
        if (!tenantUnitList.isEmpty()) {
            tenantUnitList.forEach(tenantUnit -> tennatUnitDtoList.add(toTenantUnitDto(tenantUnit, timeZone)));
        }
        return tennatUnitDtoList;
    }

    @Mapping(source = "value", target = "value")
    @Mapping(source = "code", target = "name")
    @Mapping(source = "name", target = "label")
    @Mapping(source = "isDefault", target = "isDefault")
    OptionDto toOptionDto(TenantUnitOptionProjection tenantUnit);

    List<OptionDto> toOptionDtoList(List<TenantUnitOptionProjection> tenantUnitList);
}
