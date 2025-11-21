package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSourcingGridField;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TenantSourcingGridFieldMapper {

    TenantSourcingGridFieldMapper INSTANCE = Mappers.getMapper(TenantSourcingGridFieldMapper.class);

    SourcingGridFieldDto toSourcingGridFieldDto(TenantSourcingGridField tenantSourcingGridField);

    List<SourcingGridFieldDto> toSourcingGridFieldDto(List<TenantSourcingGridField> tenantSourcingGridFieldList);

    SourcingGridFieldSearchableDto toSourcingGridFieldSearchableDto(TenantSourcingGridField tenantSourcingGridField);

    List<SourcingGridFieldSearchableDto> toSourcingGridFieldSearchableDto(List<TenantSourcingGridField> tenantSourcingGridField);

    default SourcingGridFieldDto toSourcingGridFieldDto(TenantSourcingGridField tenantSourcingGridField, String timeZone) {
        SourcingGridFieldDto sourcingGridFieldDto = toSourcingGridFieldDto(tenantSourcingGridField);
        if (sourcingGridFieldDto != null) {

            sourcingGridFieldDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingGridFieldDto.getCreatedDate(), timeZone));
            sourcingGridFieldDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingGridFieldDto.getUpdatedDate(), timeZone));
            sourcingGridFieldDto.setRecId(tenantSourcingGridField.getSourcingGridField().getRecId());
        }
        return sourcingGridFieldDto;
    }

    default List<SourcingGridFieldDto> toSourcingGridFieldDtoList(List<TenantSourcingGridField> tenantSourcingGridFieldList, String timeZone) {
        List<SourcingGridFieldDto> sourcingGridFieldDtoList = new ArrayList();
        if(tenantSourcingGridFieldList != null) {
            tenantSourcingGridFieldList.forEach(i -> sourcingGridFieldDtoList.add(toSourcingGridFieldDto(i, timeZone)));
        }
        return sourcingGridFieldDtoList;
    }
}
