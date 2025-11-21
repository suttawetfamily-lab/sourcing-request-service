package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemSubCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSubCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemSubCategoryKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemSubCategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SubCategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantSubCategoryRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemSubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestItemSubCategoryServiceImpl implements RequestItemSubCategoryService {

    private final RequestItemSubCategoryRepository requestItemSubCategoryRepository;

    private final SubCategoryRepository subCategoryRepository;

    private final TenantSubCategoryRepository tenantSubCategoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer subCategoryId, RequestItem requestItem) {
        Optional<RequestItemSubCategory> existingRequestItemSubCategory =
                requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItem.getRecId());

        if (existingRequestItemSubCategory.isPresent()) {
            if (existingRequestItemSubCategory.get().getSubCategory().getId() == Long.valueOf(subCategoryId)) return;
            requestItemSubCategoryRepository.delete(existingRequestItemSubCategory.get());
        }

        if (subCategoryId == null) return;

        TenantSubCategory subCategory = tenantSubCategoryRepository.findById(Long.valueOf(subCategoryId))
                .orElseThrow(() -> new BusinessException(ApiMessage.E7067, ApiMessage.E7067.description()));

        RequestItemSubCategory requestSubCategory = RequestItemSubCategory.builder()
                .id(new RequestItemSubCategoryKey())
                .requestItem(requestItem)
                .subCategory(subCategory)
                .subCategoryCode(subCategory.getSubCategoryCode())
                .subCategoryName(subCategory.getSubCategoryName())
                .build();

        requestItemSubCategoryRepository.save(requestSubCategory);
    }
}
