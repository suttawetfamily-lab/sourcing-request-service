package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemV2Dto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {OptionDtoMapper.class})
public interface RequestItemMapper {

    RequestItemMapper INSTANCE = Mappers.getMapper(RequestItemMapper.class);

    @Mapping(target = "unitId", source = "unit.recId")
    @Mapping(target = "unitCode", source = "unit.code")
    @Mapping(target = "sourcingTypeId", source = "sourcingType.recId")
    RequestItemDto toRequestItemDto(RequestItem requestItem);

    @Mapping(target = "sourcingTypeId", source = "sourcingType.recId")
    @Mapping(target = "unitObj", source = "unit")
    @Mapping(target = "deliveryLocation", ignore = true)
    RequestItemV2Dto toRequestItemV2Dto(RequestItem requestItem);

    default RequestItemDto toRequestItemDto(RequestItem requestItem, String timeZone) {
        RequestItemDto requestItemDto = toRequestItemDto(requestItem);
        if (requestItemDto != null) {
            requestItemDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestItemDto.getCreatedDate(), timeZone));
            requestItemDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestItemDto.getUpdatedDate(), timeZone));
            requestItemDto.setBidValidityStartDate(DateTimeUtil.convertTimestampByUserTimeZone(requestItemDto.getBidValidityStartDate(), timeZone));
            requestItemDto.setBidValidityEndDate(DateTimeUtil.convertTimestampByUserTimeZone(requestItemDto.getBidValidityEndDate(), timeZone));
        }
        return requestItemDto;
    }

    default RequestItemV2Dto toRequestItemV2Dto(RequestItem requestItem, String timeZone) {
        RequestItemV2Dto requestItemDto = toRequestItemV2Dto(requestItem);
        if (requestItemDto != null) {
            requestItemDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestItemDto.getCreatedDate(), timeZone));
            requestItemDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestItemDto.getUpdatedDate(), timeZone));
            requestItemDto.setBidValidityStartDate(DateTimeUtil.convertTimestampByUserTimeZoneStr(requestItem.getBidValidityStartDate(), timeZone));
            requestItemDto.setBidValidityEndDate(DateTimeUtil.convertTimestampByUserTimeZoneStr(requestItem.getBidValidityEndDate(), timeZone));
        }
        return requestItemDto;
    }

    default List<RequestItemDto> toRequestItemDtoList(List<RequestItem> requestItemList, String timeZone) {
        List<RequestItemDto> requestItemDtoList = new ArrayList();
        if (requestItemList != null) {
            requestItemList.forEach(i -> requestItemDtoList.add(toRequestItemDto(i, timeZone)));
        }
        return requestItemDtoList;
    }

    default List<RequestItemV2Dto> toRequestItemV2DtoList(List<RequestItem> requestItemList, String timeZone) {
        List<RequestItemV2Dto> requestItemDtoList = new ArrayList();
        if (requestItemList != null) {
            requestItemList.forEach(i -> requestItemDtoList.add(toRequestItemV2Dto(i, timeZone)));
        }
        return requestItemDtoList;
    }
}