package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestStatus;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SourcingStatusMapper {

    SourcingStatusMapper INSTANCE = Mappers.getMapper(SourcingStatusMapper.class);

    SourcingStatusDto toSourcingStatusDto(SourcingStatus sourcingStatus);


    @Mapping(target = "value", source = "recId")
    @Mapping(target = "name", source = "code")
    @Mapping(target = "label", source = "description")
    OptionDto sourcingStatusToOptionDto(SourcingStatus sourcingStatus);

    List<OptionDto> sourcingStatusToOptionDtoList(List<SourcingStatus> sourcingStatuses);

    default SourcingStatusDto toSourcingStatusDto(SourcingStatus sourcingStatus, String timeZone) {
        SourcingStatusDto sourcingStatusDto = toSourcingStatusDto(sourcingStatus);
        if (sourcingStatusDto != null) {
            sourcingStatusDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingStatusDto.getCreatedDate(), timeZone));
        }
        return sourcingStatusDto;
    }
}
