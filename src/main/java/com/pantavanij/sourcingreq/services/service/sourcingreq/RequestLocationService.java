package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;

public interface RequestLocationService {
    void saveOrUpdate(LocationDto locationDto, Request request);
}
