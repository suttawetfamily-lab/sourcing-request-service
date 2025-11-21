package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;

public interface RequestSubCategoryService {

    void saveOrUpdate(Integer subCategoryId, Request request);
}
