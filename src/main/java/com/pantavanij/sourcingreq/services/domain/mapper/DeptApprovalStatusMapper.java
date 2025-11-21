package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.DeptApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.DeptApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantApprovalStatus;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeptApprovalStatusMapper {

    DeptApprovalStatusMapper INSTANCE = Mappers.getMapper(DeptApprovalStatusMapper.class);

    @Mapping(source = "canApprove", target = "canApprove")
    DeptApprovalStatusDto toDeptApprovalStatusDto(DeptApprovalStatus deptApprovalStatus);

    List<DeptApprovalStatusDto> toDeptApprovalStatusDto(List<DeptApprovalStatus> deptApprovalStatusList);

    default DeptApprovalStatusDto toDeptApprovalStatusDto(DeptApprovalStatus deptApprovalStatus, String timeZone) {
        DeptApprovalStatusDto deptApprovalStatusDto = toDeptApprovalStatusDto(deptApprovalStatus);
        if (deptApprovalStatusDto != null) {
            deptApprovalStatusDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(deptApprovalStatusDto.getCreatedDate(), timeZone));
        }
        return deptApprovalStatusDto;
    }

}
