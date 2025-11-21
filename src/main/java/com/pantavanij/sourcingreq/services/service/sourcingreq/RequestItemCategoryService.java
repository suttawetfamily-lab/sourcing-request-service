package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;

public interface RequestItemCategoryService {

    void saveOrUpdate(Long categoryId, RequestItem requestItem);
}
