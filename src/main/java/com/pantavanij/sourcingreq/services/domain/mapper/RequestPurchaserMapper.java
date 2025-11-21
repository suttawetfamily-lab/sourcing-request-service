package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestPurchaserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestPurchaser;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface RequestPurchaserMapper {

    RequestPurchaserMapper INSTANCE = Mappers.getMapper(RequestPurchaserMapper.class);

    RequestPurchaserDto toRequestPurchaserDto(RequestPurchaser requestPurchaser);

    default RequestPurchaserDto toRequestPurchaserDto(RequestPurchaser requestPurchaser, String timeZone) {
        RequestPurchaserDto requestPurchaserDto = toRequestPurchaserDto(requestPurchaser);
        if (requestPurchaserDto != null) {
            requestPurchaserDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestPurchaserDto.getCreatedDate(), timeZone));
            requestPurchaserDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestPurchaserDto.getUpdatedDate(), timeZone));
        }
        return requestPurchaserDto;
    }

    default List<RequestPurchaserDto> toRequestPurchaserDtoList(List<RequestPurchaser> requestPurchaserList, String timeZone) {
        List<RequestPurchaserDto> requestPurchaserDtoList = new ArrayList();
        if (requestPurchaserList != null) {
            requestPurchaserList.forEach(i -> requestPurchaserDtoList.add(toRequestPurchaserDto(i, timeZone)));
        }
        return requestPurchaserDtoList;
    }
}
