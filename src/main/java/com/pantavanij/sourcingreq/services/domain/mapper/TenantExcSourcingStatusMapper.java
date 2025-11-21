package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantExcSourcingStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantExcSourcingStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TenantExcSourcingStatusMapper {

    TenantExcSourcingStatusMapper INSTANCE = Mappers.getMapper(TenantExcSourcingStatusMapper.class);

    @Mapping(source = "id.excSourcingStatusId", target = "recId")
    @Mapping(source = "name", target = "code")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "canEdit", target = "canEdit")
    @Mapping(source = "canDelete", target = "canDelete")
    @Mapping(source = "canReject", target = "canReject")
    @Mapping(source = "canViewHistory", target = "canViewHistory")
    @Mapping(target = "redirectURL", constant = "")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(target = "showModalMsgInfo", constant = "false")
    @Mapping(target = "removedLastItem", constant = "false")
    SourcingStatusDto toSourcingStatusDto(TenantExcSourcingStatus entity);

    // ✅ เพิ่ม mapping ที่เหมือนกันสำหรับ DTO ที่ controller ใช้
    @Mapping(source = "id.excSourcingStatusId", target = "recId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    TenantExcSourcingStatusDto toTenantExcSourcingStatusDto(TenantExcSourcingStatus entity);

    List<TenantExcSourcingStatusDto> toTenantExcSourcingStatusDto(List<TenantExcSourcingStatus> entityList);
}
