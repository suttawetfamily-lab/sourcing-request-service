package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingDeptApproverDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcingApprover;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {RequestItemMapper.class, ApproverMapper.class})
public interface ExcSourcingApproverMapper {

    ExcSourcingApproverMapper INSTANCE = Mappers.getMapper(ExcSourcingApproverMapper.class);

//    @Mapping(target = "request.requestItemList.deliveryLocation", ignore = true)
    @Mapping(target = "approverDto", expression = "java(ApproverMapper.INSTANCE.toApproverDto(excSourcingApprover.getApprover()))")
    ExcSourcingDeptApproverDto toExcSourcingDeptApproverDto(ExcSourcingApprover excSourcingApprover);

    default ExcSourcingDeptApproverDto toExcSourcingDeptApproverDto(ExcSourcingApprover excSourcingApprover, String timeZone) {
        ExcSourcingDeptApproverDto excSourcingDeptApproverDto = toExcSourcingDeptApproverDto(excSourcingApprover);
        if (excSourcingDeptApproverDto != null) {
            excSourcingDeptApproverDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(excSourcingDeptApproverDto.getCreatedDate(), timeZone));
            excSourcingDeptApproverDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(excSourcingDeptApproverDto.getUpdatedDate(), timeZone));
        }
        return excSourcingDeptApproverDto;
    }

    default List<ExcSourcingDeptApproverDto> toExcSourcingDeptApproverDtoList(List<ExcSourcingApprover> excSourcingApproverList, String timeZone) {
        List<ExcSourcingDeptApproverDto> excSourcingDeptApproverDtoList = new ArrayList();
        if (excSourcingApproverList != null) {
            excSourcingApproverList.forEach(i -> excSourcingDeptApproverDtoList.add(toExcSourcingDeptApproverDto(i, timeZone)));
        }
        return excSourcingDeptApproverDtoList;
    }
}
