package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface TenantRequestGridFieldMapper {

    TenantRequestGridFieldMapper INSTANCE = Mappers.getMapper(TenantRequestGridFieldMapper.class);

    RequestGridFieldDto toRequestGridFieldDto(TenantRequestGridField tenantRequestGridField);

    List<RequestGridFieldDto> toRequestGridFieldDto(List<TenantRequestGridField> tenantRequestGridFieldList);

    RequestGridFieldSearchableDto toRequestGridFieldSearchableDto(TenantRequestGridField tenantRequestGridField);

    List<RequestGridFieldSearchableDto> toRequestGridFieldSearchableDto(List<TenantRequestGridField> tenantRequestGridField);

    default RequestGridFieldDto toRequestGridFieldDto(TenantRequestGridField tenantRequestGridField, String timeZone) {
        RequestGridFieldDto requestGridFieldDto = toRequestGridFieldDto(tenantRequestGridField);
        if (requestGridFieldDto != null) {

            requestGridFieldDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestGridFieldDto.getCreatedDate(), timeZone));
            requestGridFieldDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestGridFieldDto.getUpdatedDate(), timeZone));
            requestGridFieldDto.setRecId(tenantRequestGridField.getRequestGridField().getRecId());
        }
        return requestGridFieldDto;
    }

    default List<RequestGridFieldDto> toRequestGridFieldDtoList(List<TenantRequestGridField> tenantRequestGridFieldList, String timeZone) {
        List<RequestGridFieldDto> requestGridFieldDtoList = new ArrayList();
        if(tenantRequestGridFieldList != null) {
            tenantRequestGridFieldList.forEach(i -> requestGridFieldDtoList.add(toRequestGridFieldDto(i, timeZone)));
        }
        return requestGridFieldDtoList;
    }
}
