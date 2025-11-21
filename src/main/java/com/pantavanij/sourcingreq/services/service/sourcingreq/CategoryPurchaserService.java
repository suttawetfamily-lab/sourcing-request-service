package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.CategoryPurchaserDto;

import java.util.List;

public interface CategoryPurchaserService {
    List<CategoryPurchaserDto> getCategoryPurchaserByTenant(Integer tenantId);
}
