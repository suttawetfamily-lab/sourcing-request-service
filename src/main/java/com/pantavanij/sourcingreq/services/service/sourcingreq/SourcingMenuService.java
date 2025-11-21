package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingMenuDto;

import java.util.List;

public interface SourcingMenuService {

    List<SourcingMenuDto> getSourcingMenuByTenantId(Integer tenantId);
}
