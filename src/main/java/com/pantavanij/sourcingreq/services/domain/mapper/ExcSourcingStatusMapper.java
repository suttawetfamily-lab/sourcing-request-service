package com.pantavanij.sourcingreq.services.domain.mapper;


import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcingStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantExcSourcingStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface ExcSourcingStatusMapper {
    ExcSourcingStatusMapper INSTANCE = Mappers.getMapper(ExcSourcingStatusMapper.class);

    ExcSourcingStatusDto toSourcingStatusDto(ExcSourcingStatus excSourcingStatus);

    @Mapping(target = "name", source = "tenantExcSourcingStatus.name")
    @Mapping(target = "recId", source = "tenantExcSourcingStatus.excSourcingStatus.recId")
    ExcSourcingStatusNameDto toExcSourcingStatusNameDto(TenantExcSourcingStatus tenantExcSourcingStatus);

    List<ExcSourcingStatusNameDto> toExcSourcingStatusNameDto(List<TenantExcSourcingStatus> tenantExcSourcingStatus);

    @Mapping(target = "value", source = "tenantExcSourcingStatus.excSourcingStatus.recId")
    @Mapping(target = "name", source = "tenantExcSourcingStatus.name")
    @Mapping(target = "label", source = "tenantExcSourcingStatus.description")
    List<OptionDto> tenantExcSourcingStatusToOptionDto(List<TenantExcSourcingStatus> tenantExcSourcingStatus);
    
    default ExcSourcingStatusDto toSourcingStatusDto(ExcSourcingStatus excSourcingStatus, String timeZone) {
        ExcSourcingStatusDto dto = toSourcingStatusDto(excSourcingStatus);
        if (dto != null && dto.getCreatedDate() != null) {
            dto.setCreatedDate(
                    com.pantavanij.sourcingreq.services.util.DateTimeUtil.convertTimestampByUserTimeZone(
                            dto.getCreatedDate(), timeZone
                    )
            );
        }
        return dto;
    }
}


