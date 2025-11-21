package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemCategory;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemCategoryRequest;

public interface ExistingPriceItemCategoryService {
    ExistingPriceItemCategory saveExistingPriceItemCategory(ExistingPriceItemCategoryRequest requestItemCategoryRequest);

    boolean deleteByExistingPriceItemAndCategory(Long requestItemId, Integer categoryId);
}
