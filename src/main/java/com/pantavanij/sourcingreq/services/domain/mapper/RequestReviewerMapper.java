package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestReviewerDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestReviewer;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {RequestItemMapper.class})
public interface RequestReviewerMapper {

    RequestReviewerMapper INSTANCE = Mappers.getMapper(RequestReviewerMapper.class);

//    @Mapping(target = "request.requestItemList.deliveryLocation", ignore = true)
    RequestReviewerDto toRequestReviewerDto(RequestReviewer requestReviewer);

    default RequestReviewerDto toRequestReviewerDto(RequestReviewer requestReviewer, String timeZone) {
        RequestReviewerDto requestReviewerDto = toRequestReviewerDto(requestReviewer);
        if (requestReviewerDto != null) {
            requestReviewerDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestReviewerDto.getCreatedDate(), timeZone));
            requestReviewerDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestReviewerDto.getUpdatedDate(), timeZone));
        }
        return requestReviewerDto;
    }

    default List<RequestReviewerDto> toRequestReviewerDtoList(List<RequestReviewer> requestReviewerList, String timeZone) {
        List<RequestReviewerDto> requestReviewerDtoList = new ArrayList();
        if (requestReviewerList != null) {
            requestReviewerList.forEach(i -> requestReviewerDtoList.add(toRequestReviewerDto(i, timeZone)));
        }
        return requestReviewerDtoList;
    }
}
