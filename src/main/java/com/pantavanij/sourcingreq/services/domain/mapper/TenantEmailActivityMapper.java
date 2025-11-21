package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

@Mapper(imports = DateTimeUtil.class)
public interface TenantEmailActivityMapper {
    TenantEmailActivityMapper INSTANCE = Mappers.getMapper(TenantEmailActivityMapper.class);
    TenantEmailActivityDto toTenantEmailActivityDto(TenantEmailActivity tenantEmailActivity);
    SendMailToRoleDto toSendMailToRoleDto(TenantEmailActivity tenantEmailActivity);

    default TenantEmailActivityDto toTenantEmailActivityDto(TenantEmailActivity tenantEmailActivity, String timeZone) {
        TenantEmailActivityDto tenantEmailActivityDto = toTenantEmailActivityDto(tenantEmailActivity);
        if (tenantEmailActivityDto != null) {
            tenantEmailActivityDto.setCreatedDate(DateTimeUtil.convertTimestampByUserTimeZone(tenantEmailActivityDto.getCreatedDate(), timeZone));
            tenantEmailActivityDto.setUpdatedDate(DateTimeUtil.convertTimestampByUserTimeZone(tenantEmailActivityDto.getUpdatedDate(), timeZone));
        }
        return tenantEmailActivityDto;
    }

}
