package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemSubCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SubCategory;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemSubCategoryRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ExistingPriceItemRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ExistingPriceItemSubCategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SubCategoryRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ExistingPriceItemSubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExistingPriceItemSubCategoryServiceImpl implements ExistingPriceItemSubCategoryService {

    private final ExistingPriceItemRepository existingPriceItemRepository;

    private final ExistingPriceItemSubCategoryRepository existingPriceItemSubCategoryRepository;

    private final SubCategoryRepository subCategoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExistingPriceItemSubCategory saveExistingPriceItemSubCategory(ExistingPriceItemSubCategoryRequest existingPriceItemSubCategoryRequest) {
        ExistingPriceItemSubCategory existingPriceItemSubCategory = setExistingPriceItemSubCategory(existingPriceItemSubCategoryRequest);
        ExistingPriceItemSubCategory existingExistingPriceItemSubCategory = existingPriceItemSubCategoryRepository.findExistingPriceItemSubCategoryByExistingPriceItemAndSubCategory(existingPriceItemSubCategory.getExistingPriceItem(), existingPriceItemSubCategory.getSubCategory());

        if(existingExistingPriceItemSubCategory == null) {
            existingPriceItemSubCategoryRepository.saveExistingPriceItemSubCategory(existingPriceItemSubCategoryRequest.getExistingPriceItemId(),
                    existingPriceItemSubCategoryRequest.getSubCategoryId(),
                    existingPriceItemSubCategoryRequest.getSubCategoryCode(),
                    existingPriceItemSubCategoryRequest.getSubCategoryName());
        }
        else {
            existingPriceItemSubCategoryRepository.updateExistingPriceItemSubCategory(existingPriceItemSubCategoryRequest.getExistingPriceItemId(),
                    existingPriceItemSubCategoryRequest.getSubCategoryId(),
                    existingPriceItemSubCategoryRequest.getSubCategoryCode(),
                    existingPriceItemSubCategoryRequest.getSubCategoryName());
        }
        return existingPriceItemSubCategory;
    }

    private ExistingPriceItemSubCategory setExistingPriceItemSubCategory(ExistingPriceItemSubCategoryRequest existingPriceItemSubCategoryRequest){
        ExistingPriceItem existingPriceItem = existingPriceItemRepository.findExistingPriceItemByRecId(existingPriceItemSubCategoryRequest.getExistingPriceItemId());
        SubCategory subCategory = subCategoryRepository.findSubCategoryByRecId(existingPriceItemSubCategoryRequest.getSubCategoryId());

        ExistingPriceItemSubCategory existingPriceItemSubCategory = new ExistingPriceItemSubCategory();

        existingPriceItemSubCategory.setExistingPriceItem(existingPriceItem);
        existingPriceItemSubCategory.setSubCategory(subCategory);
        existingPriceItemSubCategory.setSubCategoryCode(existingPriceItemSubCategoryRequest.getSubCategoryCode());
        existingPriceItemSubCategory.setSubCategoryName(existingPriceItemSubCategoryRequest.getSubCategoryName());

        return existingPriceItemSubCategory;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByExistingPriceItemAndSubCategory(Long requestId, Integer subCategoryId) {
        try {
            ExistingPriceItem existingPriceItem = existingPriceItemRepository.findExistingPriceItemByRecId(requestId);
            SubCategory subCategory = subCategoryRepository.findSubCategoryByRecId(subCategoryId);
            existingPriceItemSubCategoryRepository.deleteExistingPriceItemSubCategoryByExistingPriceItemAndSubCategory(existingPriceItem, subCategory);
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }
}
