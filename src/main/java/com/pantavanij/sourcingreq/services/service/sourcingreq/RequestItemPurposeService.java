package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;

public interface RequestItemPurposeService {
    void saveOrUpdate(Integer purposeId, RequestItem requestItem);
}
