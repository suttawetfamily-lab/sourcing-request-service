package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;

public interface RequestBudgetRefNoService {
    void saveOrUpdate(Integer budgetRefNoId, Request request);
}
