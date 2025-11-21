package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;

public interface RequestTypeService {

    void saveOrUpdate(Integer typeId, Request request);
}
