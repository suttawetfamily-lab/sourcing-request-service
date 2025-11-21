package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestHistoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestHistory;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface RequestHistoryMapper {

    RequestHistoryMapper INSTANCE = Mappers.getMapper(RequestHistoryMapper.class);

    RequestHistoryDto toRequestHistoryDto(RequestHistory requestHistory);


    default RequestHistoryDto toRequestHistoryDto(RequestHistory requestHistory, String timeZone) {
        RequestHistoryDto requestHistoryDto = toRequestHistoryDto(requestHistory);
        if (requestHistoryDto != null) {
            requestHistoryDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestHistoryDto.getCreatedDate(), timeZone));
        }
        return requestHistoryDto;
    }

    default List<RequestHistoryDto> toRequestHistoryDtoList(List<RequestHistory> requestHistoryList, String timeZone) {
        List<RequestHistoryDto> requestHistoryDtoList = new ArrayList();
        if (requestHistoryList != null) {
            requestHistoryList.forEach(i -> requestHistoryDtoList.add(toRequestHistoryDto(i, timeZone)));
        }
        return requestHistoryDtoList;
    }
}
