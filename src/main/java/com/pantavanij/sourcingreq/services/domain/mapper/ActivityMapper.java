package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ActivityDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Activity;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(imports = DateTimeUtil.class)
public interface ActivityMapper {

    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);

    ActivityDto toActivityDto(Activity activity);

    default ActivityDto toActivityDto(Activity activity, String timeZone) {
        ActivityDto activityDto = toActivityDto(activity);
        if (activityDto != null) {
            activityDto.setCreatedDate(DateTimeUtil.convertTimestampByUserTimeZone(activityDto.getCreatedDate(), timeZone));
            activityDto.setUpdatedDate(DateTimeUtil.convertTimestampByUserTimeZone(activityDto.getUpdatedDate(), timeZone));
        }
        return activityDto;
    }
}
