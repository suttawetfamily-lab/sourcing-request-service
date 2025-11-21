package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.RequestDeptApproverDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestApprover;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {RequestItemMapper.class, ApproverMapper.class})
public interface RequestApproverMapper {

    RequestApproverMapper INSTANCE = Mappers.getMapper(RequestApproverMapper.class);

//    @Mapping(target = "request.requestItemList.deliveryLocation", ignore = true)
    @Mapping(target = "approverDto", expression = "java(ApproverMapper.INSTANCE.toApproverDto(requestApprover.getApprover()))")
    RequestDeptApproverDto toRequestDeptApproverDto(RequestApprover requestApprover);

    default RequestDeptApproverDto toRequestDeptApproverDto(RequestApprover requestApprover, String timeZone) {
        RequestDeptApproverDto requestDeptApproverDto = toRequestDeptApproverDto(requestApprover);
        if (requestDeptApproverDto != null) {
            requestDeptApproverDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestDeptApproverDto.getCreatedDate(), timeZone));
            requestDeptApproverDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestDeptApproverDto.getUpdatedDate(), timeZone));
        }
        return requestDeptApproverDto;
    }

    default List<RequestDeptApproverDto> toRequestDeptApproverDtoList(List<RequestApprover> requestApproverList, String timeZone) {
        List<RequestDeptApproverDto> requestDeptApproverDtoList = new ArrayList();
        if (requestApproverList != null) {
            requestApproverList.forEach(i -> requestDeptApproverDtoList.add(toRequestDeptApproverDto(i, timeZone)));
        }
        return requestDeptApproverDtoList;
    }
}
