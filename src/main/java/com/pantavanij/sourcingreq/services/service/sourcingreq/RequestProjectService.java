package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;

public interface RequestProjectService {
    void saveOrUpdate(String projectCode, Request request, Integer tenantId);
}
