package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantApprovalStatus;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ApprovalStatusMapper {

    ApprovalStatusMapper INSTANCE = Mappers.getMapper(ApprovalStatusMapper.class);

    ApprovalStatusDto toApprovalStatusDto(ApprovalStatus approvalStatus);

    @Mapping(target = "recId", source = "tenantApprovalStatus.approvalStatus.recId")
    ApprovalStatusDto toApprovalStatusDto(TenantApprovalStatus tenantApprovalStatus);

    @Mapping(target = "name", source = "tenantApprovalStatus.name")
    @Mapping(target = "recId", source = "tenantApprovalStatus.approvalStatus.recId")
    ApprovalStatusNameDto toApprovalStatusNameDto(TenantApprovalStatus tenantApprovalStatus);

    List<ApprovalStatusNameDto> toApprovalStatusNameDto(List<TenantApprovalStatus> tenantApprovalStatusList);

    default ApprovalStatusDto toApprovalStatusDto(ApprovalStatus approvalStatus, String timeZone) {
        ApprovalStatusDto approvalStatusDto = toApprovalStatusDto(approvalStatus);
        if (approvalStatusDto != null) {
            approvalStatusDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approvalStatusDto.getCreatedDate(), timeZone));
        }
        return approvalStatusDto;
    }

    default ApprovalStatusDto toApprovalStatusDto(TenantApprovalStatus tenantApprovalStatus, String timeZone) {
        ApprovalStatusDto approvalStatusDto = toApprovalStatusDto(tenantApprovalStatus);
        if (approvalStatusDto != null) {
            approvalStatusDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approvalStatusDto.getCreatedDate(), timeZone));
        }
        return approvalStatusDto;
    }

    default List<ApprovalStatusDto> toApprovalStatusDtoList(List<ApprovalStatus> approvalStatusList, String timeZone) {
        List<ApprovalStatusDto> approvalStatusDtoList = new ArrayList();
        if (approvalStatusList != null) {
            approvalStatusList.forEach(i -> approvalStatusDtoList.add(toApprovalStatusDto(i, timeZone)));
        }
        return approvalStatusDtoList;
    }

}
