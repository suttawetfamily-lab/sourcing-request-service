package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantApprovalStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TenantApprovalStatusMapper {

    TenantApprovalStatusMapper INSTANCE = Mappers.getMapper(TenantApprovalStatusMapper.class);

    @Mapping(source = "approvalStatus.recId", target = "recId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "canEdit", target = "canEdit")
    @Mapping(source = "canDelete", target = "canDelete")
    @Mapping(source = "canDuplicate", target = "canDuplicate")
    @Mapping(source = "canCancel", target = "canCancel")
    @Mapping(source = "canCopyToPR", target = "canCopyToPR")
    @Mapping(source = "canViewHistory", target = "canViewHistory")
    // flag พิเศษ ที่ไม่มีใน TenantApprovalStatus → กำหนด ignore ไว้
    @Mapping(target = "canApprove", ignore = true)
    @Mapping(target = "canAssignToMe", ignore = true)
    @Mapping(target = "canForwardApprovalWorkflow", ignore = true)
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "createdDate", target = "createdDate")
    ApprovalStatusDto toApprovalStatusDto(TenantApprovalStatus entity);
}
