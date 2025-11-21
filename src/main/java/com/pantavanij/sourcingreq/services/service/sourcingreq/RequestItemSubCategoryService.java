package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;

public interface RequestItemSubCategoryService {

    void saveOrUpdate(Integer subCategoryId, RequestItem requestItem);
}
