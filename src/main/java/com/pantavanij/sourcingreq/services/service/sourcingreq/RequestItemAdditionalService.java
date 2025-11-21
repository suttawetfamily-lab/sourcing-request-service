package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ShortlistDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemAdditional;

public interface RequestItemAdditionalService {
    RequestItemAdditional saveOrUpdate(RequestItem requestItem, ShortlistDto shortlist, String timeZone);
}
