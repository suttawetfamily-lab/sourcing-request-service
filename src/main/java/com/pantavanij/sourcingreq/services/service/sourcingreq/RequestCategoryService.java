package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;

public interface RequestCategoryService {
    void saveOrUpdate(Integer categoryId, Request request);
}
