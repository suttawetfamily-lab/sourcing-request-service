package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import javax.validation.*;

public interface TenantService {
    Tenant findByCode(String code);

    Tenant getTenantUnitByRecId(Integer recId);

    TenantDto getTenantDtoByRecId(Integer recId, String timeZone);

    TenantSearchDto searchTenantListByCondition(TenantSearchRequest request, Pageable pageable, String timeZone);

    Integer createTenant(TenantRequest request);

    Integer updateTenant(TenantRequest request);
}
