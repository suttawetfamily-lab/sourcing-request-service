package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.ActivityDto;
import com.pantavanij.sourcingreq.services.domain.response.ActivityResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping(value = "/activity/{activityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getActivityByRecId(@PathVariable Integer activityId) {
        ActivityDto activity = activityService.getActivityByRecId(activityId);
        if (activity != null) {
            return ResponseEntity.ok().body(ActivityResponse.builder().activityDto(activity).build());
        } else {
            return ResponseEntity.ok().body(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()));
        }
    }

}