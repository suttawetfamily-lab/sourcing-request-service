package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailDataSourceDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetailDataSource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TenantSectionDetailDataSourceMapper {

    TenantSectionDetailDataSourceMapper INSTANCE = Mappers.getMapper(TenantSectionDetailDataSourceMapper.class);

    TenantSectionDetailDataSourceDto toTenantSectionDetailDataSourceDto(TenantSectionDetailDataSource tenantSectionDetailDataSource);
    List<TenantSectionDetailDataSourceDto> toTenantSectionDetailDataSourceDto(List<TenantSectionDetailDataSource> tenantSectionDetailDataSourceList);
}
