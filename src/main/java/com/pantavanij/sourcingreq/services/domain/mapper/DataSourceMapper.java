package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.DataSourceDto;
import com.pantavanij.sourcingreq.services.domain.dto.DataSourceObjDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestTenantDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.DataSource;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface DataSourceMapper {

    DataSourceMapper INSTANCE = Mappers.getMapper(DataSourceMapper.class);

    //@Mapping(source = "tenant.recId", target = "tenantId")
    DataSourceObjDto toDataSourceObjDto(DataSource dataSource);



}
