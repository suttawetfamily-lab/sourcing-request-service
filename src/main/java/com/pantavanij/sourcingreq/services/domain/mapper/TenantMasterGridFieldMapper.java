package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.MasterGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.MasterGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantMasterGridField;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TenantMasterGridFieldMapper {

    TenantMasterGridFieldMapper INSTANCE = Mappers.getMapper(TenantMasterGridFieldMapper.class);

    MasterGridFieldDto toMasterGridFieldDto(TenantMasterGridField tenantMasterGridField);

    List<MasterGridFieldDto> toMasterGridFieldDto(List<TenantMasterGridField> tenantMasterGridFieldList);

    MasterGridFieldSearchableDto toMasterGridFieldSearchableDto(TenantMasterGridField tenantMasterGridField);

    List<MasterGridFieldSearchableDto> toMasterGridFieldSearchableDto(List<TenantMasterGridField> tenantMasterGridField);

}
