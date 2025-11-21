package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;

public interface TenantSectionDetailDataSourceService {
    void saveOrUpdate(Integer dataSourceId, TenantSectionDetail tenantSectionDetail);
}
