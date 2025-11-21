package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Category;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemCategory;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemCategoryRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.CategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ExistingPriceItemCategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ExistingPriceItemRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ExistingPriceItemCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExistingPriceItemCategoryServiceImpl implements ExistingPriceItemCategoryService {

    private final ExistingPriceItemRepository existingPriceItemRepository;

    private final ExistingPriceItemCategoryRepository existingPriceItemCategoryRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExistingPriceItemCategory saveExistingPriceItemCategory(ExistingPriceItemCategoryRequest existingPriceItemCategoryRequest) {
        ExistingPriceItemCategory existingPriceItemCategory = setExistingPriceItemCategory(existingPriceItemCategoryRequest);
        ExistingPriceItemCategory existingExistingPriceItemCategory = existingPriceItemCategoryRepository.findExistingPriceItemCategoryByExistingPriceItemAndCategory(existingPriceItemCategory.getExistingPriceItem(), existingPriceItemCategory.getCategory());

        if(existingExistingPriceItemCategory == null) {
            existingPriceItemCategoryRepository.saveExistingPriceItemCategory(existingPriceItemCategoryRequest.getExistingPriceItemId(),
                    existingPriceItemCategoryRequest.getCategoryId(),
                    existingPriceItemCategoryRequest.getCategoryCode(),
                    existingPriceItemCategoryRequest.getCategoryName());
        }
        else {
            existingPriceItemCategoryRepository.updateExistingPriceItemCategory(existingPriceItemCategoryRequest.getExistingPriceItemId(),
                    existingPriceItemCategoryRequest.getCategoryId(),
                    existingPriceItemCategoryRequest.getCategoryCode(),
                    existingPriceItemCategoryRequest.getCategoryName());
        }
        return existingPriceItemCategory;
    }

    private ExistingPriceItemCategory setExistingPriceItemCategory(ExistingPriceItemCategoryRequest existingPriceItemCategoryRequest){
        ExistingPriceItem existingPriceItem = existingPriceItemRepository.findExistingPriceItemByRecId(existingPriceItemCategoryRequest.getExistingPriceItemId());
        Category category = categoryRepository.findCategoryByRecId(existingPriceItemCategoryRequest.getCategoryId());

        ExistingPriceItemCategory existingPriceItemCategory = new ExistingPriceItemCategory();

        existingPriceItemCategory.setExistingPriceItem(existingPriceItem);
        existingPriceItemCategory.setCategory(category);
        existingPriceItemCategory.setCategoryCode(existingPriceItemCategoryRequest.getCategoryCode());
        existingPriceItemCategory.setCategoryName(existingPriceItemCategoryRequest.getCategoryName());

        return existingPriceItemCategory;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByExistingPriceItemAndCategory(Long requestId, Integer categoryId) {
        try {
            ExistingPriceItem existingPriceItem = existingPriceItemRepository.findExistingPriceItemByRecId(requestId);
            Category category = categoryRepository.findCategoryByRecId(categoryId);
            existingPriceItemCategoryRepository.deleteExistingPriceItemCategoryByExistingPriceItemAndCategory(existingPriceItem, category);
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }
}
