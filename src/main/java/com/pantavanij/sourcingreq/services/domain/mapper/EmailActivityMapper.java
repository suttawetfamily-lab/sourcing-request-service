package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

import java.util.*;

@Mapper(imports = DateTimeUtil.class)
public interface EmailActivityMapper {
    EmailActivityMapper INSTANCE = Mappers.getMapper(EmailActivityMapper.class);
    EmailActivityDto toEmailActivityDto(EmailActivity emailActivity);

    default EmailActivityDto toEmailActivityDto(EmailActivity emailActivity, String timeZone) {
        EmailActivityDto emailActivityDto = toEmailActivityDto(emailActivity);
        if (emailActivityDto != null) {
            emailActivityDto.setCreatedDate(DateTimeUtil.convertTimestampByUserTimeZone(emailActivity.getCreatedDate(), timeZone));
            emailActivityDto.setUpdatedDate(DateTimeUtil.convertTimestampByUserTimeZone(emailActivity.getUpdatedDate(), timeZone));
        }
        return emailActivityDto;
    }

    default List<EmailActivityDto> toEmailActivityDtoList(List<EmailActivity> emailActivityList, String timeZone) {
        List<EmailActivityDto> emailActivityDtoList = new ArrayList<>();
        if (!emailActivityList.isEmpty()) {
            emailActivityList.forEach(emailActivity -> emailActivityDtoList.add(toEmailActivityDto(emailActivity, timeZone)));
        }
        return emailActivityDtoList;
    }
}
