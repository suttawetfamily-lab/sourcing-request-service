package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingPurchaserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcingPurchaser;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {RequestItemMapper.class, ApproverMapper.class})
public interface ExcSourcingPurchaserMapper {

    ExcSourcingPurchaserMapper INSTANCE = Mappers.getMapper(ExcSourcingPurchaserMapper.class);

//    @Mapping(target = "request.requestItemList.deliveryLocation", ignore = true)
    @Mapping(target = "approverDto", expression = "java(ApproverMapper.INSTANCE.toApproverDto(excSourcingPurchaser.getApprover()))")
    ExcSourcingPurchaserDto toExcSourcingPurchaserDto(ExcSourcingPurchaser excSourcingPurchaser);

    default ExcSourcingPurchaserDto toExcSourcingPurchaserDto(ExcSourcingPurchaser excSourcingPurchaser, String timeZone) {
        ExcSourcingPurchaserDto excSourcingPurchaserDto = toExcSourcingPurchaserDto(excSourcingPurchaser);
        if (excSourcingPurchaserDto != null) {
            excSourcingPurchaserDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(excSourcingPurchaserDto.getCreatedDate(), timeZone));
            excSourcingPurchaserDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(excSourcingPurchaserDto.getUpdatedDate(), timeZone));
        }
        return excSourcingPurchaserDto;
    }

    default List<ExcSourcingPurchaserDto> toExcSourcingPurchaserDtoList(List<ExcSourcingPurchaser> excSourcingPurchaserList, String timeZone) {
        List<ExcSourcingPurchaserDto> excSourcingPurchaserDtoList = new ArrayList();
        if (excSourcingPurchaserList != null) {
            excSourcingPurchaserList.forEach(i -> excSourcingPurchaserDtoList.add(toExcSourcingPurchaserDto(i, timeZone)));
        }
        return excSourcingPurchaserDtoList;
    }
}
