package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;

public interface RequestItemCurrencyService {

    void saveOrUpdate(Integer currencyId, RequestItem requestItem);
}
