package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestRequesterDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestRequester;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {RequestItemMapper.class})
public interface RequestRequesterMapper {

    RequestRequesterMapper INSTANCE = Mappers.getMapper(RequestRequesterMapper.class);

//    @Mapping(target = "request.requestItemList.deliveryLocation", ignore = true)
    RequestRequesterDto toRequestRequesterDto(RequestRequester requestRequester);

    default RequestRequesterDto toRequestRequesterDto(RequestRequester requestRequester, String timeZone) {
        RequestRequesterDto requestRequesterDto = toRequestRequesterDto(requestRequester);
        if (requestRequesterDto != null) {
            requestRequesterDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestRequesterDto.getCreatedDate(), timeZone));
            requestRequesterDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestRequesterDto.getUpdatedDate(), timeZone));
        }
        return requestRequesterDto;
    }

    default List<RequestRequesterDto> toRequestRequesterDtoList(List<RequestRequester> requestRequesterList, String timeZone) {
        List<RequestRequesterDto> requestRequesterDtoList = new ArrayList();
        if (requestRequesterList != null) {
            requestRequesterList.forEach(i -> requestRequesterDtoList.add(toRequestRequesterDto(i, timeZone)));
        }
        return requestRequesterDtoList;
    }
}
