package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.CategoryPurchaserDto;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.CategoryPurchaserRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.CategoryPurchaserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryPurchaserServiceImpl implements CategoryPurchaserService {

    private final CategoryPurchaserRepository categoryPurchaserRepository;

    @Override
    public List<CategoryPurchaserDto> getCategoryPurchaserByTenant(Integer tenantId) {
        return null;
    }
}
