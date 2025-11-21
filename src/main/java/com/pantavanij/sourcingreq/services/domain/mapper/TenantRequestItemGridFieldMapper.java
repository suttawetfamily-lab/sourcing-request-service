package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemGridField;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestItemGridField;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TenantRequestItemGridFieldMapper {

    TenantRequestItemGridFieldMapper INSTANCE = Mappers.getMapper(TenantRequestItemGridFieldMapper.class);

    RequestItemGridFieldDto toRequestItemGridFieldDto(TenantRequestItemGridField tenantRequestItemGridField);

    List<RequestItemGridFieldDto> toRequestItemGridFieldDto(List<TenantRequestItemGridField> tenantRequestItemGridFieldList);

    default RequestItemGridFieldDto toRequestItemGridFieldDto(TenantRequestItemGridField tenantRequestItemGridField, String timeZone) {
        RequestItemGridFieldDto requestItemGridFieldDto = toRequestItemGridFieldDto(tenantRequestItemGridField);
        if (requestItemGridFieldDto != null) {

            requestItemGridFieldDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestItemGridFieldDto.getCreatedDate(), timeZone));
            requestItemGridFieldDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestItemGridFieldDto.getUpdatedDate(), timeZone));
            requestItemGridFieldDto.setRecId(tenantRequestItemGridField.getRequestItemGridField().getRecId());
        }
        return requestItemGridFieldDto;
    }

    default List<RequestItemGridFieldDto> toRequestItemGridFieldDtoList(List<TenantRequestItemGridField> tenantRequestItemGridFieldList, String timeZone) {
        List<RequestItemGridFieldDto> requestItemGridFieldDtoList = new ArrayList<>();
        if(tenantRequestItemGridFieldList != null) {
            tenantRequestItemGridFieldList.forEach(i -> requestItemGridFieldDtoList.add(toRequestItemGridFieldDto(i, timeZone)));
        }
        return requestItemGridFieldDtoList;
    }

}
