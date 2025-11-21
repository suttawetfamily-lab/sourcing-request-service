package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ActivityDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponse {

    private ActivityDto activityDto;
}
