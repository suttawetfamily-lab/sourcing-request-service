package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestSubCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestSubCategoryKey;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SubCategory;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestSubCategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SubCategoryRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestSubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestSubCategoryServiceImpl implements RequestSubCategoryService {

    private final RequestSubCategoryRepository requestSubCategoryRepository;

    private final SubCategoryRepository subCategoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer subCategoryId, Request request) {
        Optional<RequestSubCategory> existingRequestSubCategory = requestSubCategoryRepository.findTop1ByRequestId(request.getRecId());

        if (existingRequestSubCategory.isPresent()) {
            if (existingRequestSubCategory.get().getSubCategory().getRecId() == subCategoryId) return;
            requestSubCategoryRepository.delete(existingRequestSubCategory.get());
        }

        if (subCategoryId == null) return;

        SubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7067, ApiMessage.E7067.description()));

        RequestSubCategory requestSubCategory = RequestSubCategory.builder()
                .id(new RequestSubCategoryKey())
                .request(request)
                .subCategory(subCategory)
                .subCategoryCode(subCategory.getCode())
                .subCategoryName(subCategory.getName())
                .build();

        requestSubCategoryRepository.save(requestSubCategory);
    }
}
