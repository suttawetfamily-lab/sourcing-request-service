package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ActivityDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Activity;
import com.pantavanij.sourcingreq.services.domain.mapper.ActivityMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ActivityRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ActivityService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.Impl.ActivityServiceImpl;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceImplTest {

    private ActivityRepository activityRepository = mock(ActivityRepository.class);
    private UaaService uaaService = mock(UaaService.class);
    private ActivityService requestStatusService = new ActivityServiceImpl(activityRepository, uaaService);

    @Test
    public void getActivityByRecId_success() {
        String mockTimeZone = TestUtil.getMockTimeZone();
        Timestamp mockTimestamp = TestUtil.getMockTimestamp();
        Integer recId = 12345;

        Activity mockActivity = Activity.builder()
                .name("SAVE")
                .description("Save draft request successfully")
                .createdBy("System")
                .createdDate(mockTimestamp)
                .updatedBy("Admin")
                .updatedDate(mockTimestamp)
                .build();

        when(activityRepository.findById(recId)).thenReturn(Optional.of(mockActivity));
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);

        ActivityDto actualResult = requestStatusService.getActivityByRecId(recId);

        ActivityDto expectedResult =
                ActivityMapper.INSTANCE.toActivityDto(mockActivity, mockTimeZone);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getActivityByRecId_successWithNull() {
        Integer recId = 12345;
        String mockTimeZone = TestUtil.getMockTimeZone();

        when(activityRepository.findById(recId)).thenReturn(Optional.empty());
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);

        ActivityDto actualResult = requestStatusService.getActivityByRecId(recId);

        assertEquals(null, actualResult);
    }

}
