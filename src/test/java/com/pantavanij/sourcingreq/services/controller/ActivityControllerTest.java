package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.ActivityDto;
import com.pantavanij.sourcingreq.services.domain.response.ActivityResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ActivityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityControllerTest {

    private ActivityService activityService = mock(ActivityService.class);
    private ActivityController activityController = new ActivityController(activityService);

    @Test
    public void getActivityByRecId_success() {
        Integer recId = 12345;
        ActivityDto mockActivityDto = ActivityDto.builder().build();

        when(activityService.getActivityByRecId(recId)).thenReturn(mockActivityDto);

        ResponseEntity actualResult = activityController.getActivityByRecId(recId);

        ResponseEntity expectedResult = ResponseEntity.ok()
                .body(ActivityResponse.builder().activityDto(mockActivityDto).build());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getActivityByRecId_notFound() {
        Integer recId = 12345;
        when(activityService.getActivityByRecId(recId)).thenReturn(null);

        ResponseEntity actualResult = activityController.getActivityByRecId(recId);

        ResponseEntity expectedResult = ResponseEntity.ok()
                .body(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()));
        assertEquals(expectedResult, actualResult);
    }

}