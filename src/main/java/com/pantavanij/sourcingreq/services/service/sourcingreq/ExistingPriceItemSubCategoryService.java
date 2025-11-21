package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemSubCategory;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemSubCategoryRequest;

public interface ExistingPriceItemSubCategoryService {
    ExistingPriceItemSubCategory saveExistingPriceItemSubCategory(ExistingPriceItemSubCategoryRequest existingPriceItemSubCategoryRequest);

    boolean deleteByExistingPriceItemAndSubCategory(Long existingPriceItemId, Integer subCategoryId);
}
