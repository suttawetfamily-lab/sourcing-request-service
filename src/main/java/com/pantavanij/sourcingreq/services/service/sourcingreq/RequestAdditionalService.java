package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.RequestRequest;

public interface RequestAdditionalService {
    void saveOrUpdate(Request request, Tenant tenant, RequestRequest reqRequest);

    void delete(Request request);
}
