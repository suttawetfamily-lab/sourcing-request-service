package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemCategoryKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemCategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantCategoryRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestItemCategoryServiceImpl implements RequestItemCategoryService {

    private final RequestItemCategoryRepository requestItemCategoryRepository;
    private final TenantCategoryRepository tenantCategoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Long categoryId, RequestItem requestItem) {
        Optional<RequestItemCategory> existingRequestItemCategory =
                requestItemCategoryRepository.findTop1ByRequestItemId(requestItem.getRecId());

        if (existingRequestItemCategory.isPresent()) {
            if (existingRequestItemCategory.get().getCategory().getId() == categoryId) return;
            requestItemCategoryRepository.delete(existingRequestItemCategory.get());
        }

        if (categoryId == null) return;

        TenantCategory category = tenantCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7066, ApiMessage.E7066.description()));

        if (category == null) return;

        RequestItemCategory requestCategory = RequestItemCategory.builder()
                .id(new RequestItemCategoryKey())
                .requestItem(requestItem)
                .category(category)
                .categoryCode(category.getCategoryName())
                .categoryName(category.getCategoryName())
                .build();

        requestItemCategoryRepository.save(requestCategory);
    }
}
