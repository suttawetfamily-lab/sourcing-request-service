package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ActivityDto;

public interface ActivityService {

    ActivityDto getActivityByRecId(Integer recId);

}
