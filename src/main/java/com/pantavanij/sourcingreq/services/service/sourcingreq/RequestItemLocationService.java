package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;

public interface RequestItemLocationService {
    void saveOrUpdate(LocationDto locationDto, RequestItem requestItem);

}
