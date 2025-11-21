package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestStatus;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface RequestStatusMapper {

    RequestStatusMapper INSTANCE = Mappers.getMapper(RequestStatusMapper.class);

    RequestStatusDto toRequestStatusDto(RequestStatus requestStatus);

    @Mapping(target = "recId", source = "tenantRequestStatus.requestStatus.recId")
    RequestStatusDto toRequestStatusDto(TenantRequestStatus tenantRequestStatus);

    @Mapping(target = "name", source = "tenantRequestStatus.name")
    @Mapping(target = "recId", source = "tenantRequestStatus.requestStatus.recId")
    RequestStatusNameDto toRequestStatusNameDto(TenantRequestStatus tenantRequestStatus);

    List<RequestStatusNameDto> toRequestStatusNameDto(List<TenantRequestStatus> tenantRequestStatus);

    @Mapping(target = "value", source = "tenantRequestStatus.requestStatus.recId")
    @Mapping(target = "name", source = "tenantRequestStatus.name")
    @Mapping(target = "label", source = "tenantRequestStatus.description")
    List<OptionDto> tenantRequestStatusToOptionDto(List<TenantRequestStatus> tenantRequestStatus);
    @Mapping(target = "value", source = "tenantRequestStatus.requestStatus.recId")
    @Mapping(target = "name", source = "tenantRequestStatus.name")
    @Mapping(target = "label", source = "tenantRequestStatus.description")
    OptionDto tenantRequestStatusToOptionDto (TenantRequestStatus tenantRequestStatus);

    default RequestStatusDto toRequestStatusDto(RequestStatus requestStatus, String timeZone) {
        RequestStatusDto requestStatusDto = toRequestStatusDto(requestStatus);
        if (requestStatusDto != null) {
            requestStatusDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestStatusDto.getCreatedDate(), timeZone));
        }
        return requestStatusDto;
    }

    default RequestStatusDto toRequestStatusDto(TenantRequestStatus tenantRequestStatus, String timeZone) {
        RequestStatusDto requestStatusDto = toRequestStatusDto(tenantRequestStatus);
        if (requestStatusDto != null) {
            requestStatusDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestStatusDto.getCreatedDate(), timeZone));
        }
        return requestStatusDto;
    }

    default List<RequestStatusDto> toRequestStatusDtoList(List<RequestStatus> requestStatusList, String timeZone) {
        List<RequestStatusDto> requestStatusDtoList = new ArrayList();
        if (requestStatusList != null) {
            requestStatusList.forEach(i -> requestStatusDtoList.add(toRequestStatusDto(i, timeZone)));
        }
        return requestStatusDtoList;
    }
}
