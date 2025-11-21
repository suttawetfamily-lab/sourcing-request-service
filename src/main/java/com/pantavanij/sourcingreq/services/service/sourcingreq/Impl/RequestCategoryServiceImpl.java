package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Category;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestCategoryKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.CategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestCategoryRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestCategoryServiceImpl implements RequestCategoryService {

    private final RequestCategoryRepository requestCategoryRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer categoryId, Request request) {
        Optional<RequestCategory> existingRequestCategory = requestCategoryRepository.findTop1ByRequestId(request.getRecId());

        if (existingRequestCategory.isPresent()) {
            if (existingRequestCategory.get().getCategory().getRecId() == categoryId) return;
            requestCategoryRepository.delete(existingRequestCategory.get());
        }

        if (categoryId == null) return;

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7066, ApiMessage.E7066.description()));

        RequestCategory requestCategory = RequestCategory.builder()
                .id(new RequestCategoryKey())
                .request(request)
                .category(category)
                .categoryCode(category.getCode())
                .categoryName(category.getName())
                .build();

        requestCategoryRepository.save(requestCategory);
    }
}
