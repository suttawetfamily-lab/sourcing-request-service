package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingItemDto;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SourcingItemMapper {

    SourcingItemMapper INSTANCE = Mappers.getMapper(SourcingItemMapper.class);

    default SourcingItemDto convertTimeStampByTimeZone(SourcingItemDto sourcingItemDto, String timeZone) {
        if (sourcingItemDto != null) {
            sourcingItemDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingItemDto.getCreatedDate(), timeZone));
            sourcingItemDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingItemDto.getUpdatedDate(), timeZone));
        }
        return sourcingItemDto;
    }
}
