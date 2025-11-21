package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ActivityDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Activity;
import com.pantavanij.sourcingreq.services.domain.mapper.ActivityMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ActivityRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ActivityService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UaaService uaaService;

    @Override
    public ActivityDto getActivityByRecId(Integer recId) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Activity activity = activityRepository.findById(recId).orElse(null);
        return ActivityMapper.INSTANCE.toActivityDto(activity, timeZone);
    }
}
