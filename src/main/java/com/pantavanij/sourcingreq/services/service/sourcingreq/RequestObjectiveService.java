package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;

public interface RequestObjectiveService {
    void saveOrUpdate(Integer objectiveId, Request request);

}
