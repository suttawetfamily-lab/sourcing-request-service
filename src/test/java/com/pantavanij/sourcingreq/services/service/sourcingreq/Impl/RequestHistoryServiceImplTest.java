package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ActivityDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestHistoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Activity;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestHistory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.RequestHistoryRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ActivityRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestHistoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestHistoryServiceImplTest {
    @Mock
    private RequestHistoryRepository requestHistoryRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UaaService uaaService;
    @InjectMocks
    private RequestHistoryServiceImpl requestHistoryService;

    private List<RequestHistoryDto> requestHistoryDtoList;
    private List<RequestHistory> requestHistoryList;
    private RequestHistoryDto requestHistoryDto;
    private RequestHistory requestHistory;
    private final Long requestId = Long.valueOf(11);
    private Request request;
    private Tenant tenant;
    private ActivityDto activityDto;
    private Activity activity;
    @Before
    public void init() {
        request = new Request();
        request.setRecId(requestId);
        tenant = new Tenant();
        tenant.setRecId(1);
        activityDto = new ActivityDto();
        activityDto.setRecId(1);
        activity = new Activity();
        activity.setRecId(1);

        requestHistoryDtoList = new ArrayList<>();
        requestHistoryDto = new RequestHistoryDto();
        requestHistoryDtoList.add(requestHistoryDto);
        requestHistoryDto.setActivity(activityDto);
        requestHistoryDto.setRecId(requestId);

        requestHistoryList = new ArrayList<>();
        requestHistory = new RequestHistory();
        requestHistoryList.add(requestHistory);
        requestHistory.setRequest(request);
        requestHistory.setTenant(tenant);
        requestHistory.setActivity(activity);
        requestHistory.setRecId(requestId);
    }

    @Test
    public void testGetRequestHistoryByRequestID() {
        // arrange
        when(requestHistoryRepository.getRequestHistoryListByCondition(anyLong())).thenReturn(requestHistoryList);
        when(uaaService.getUserTimeZone(any(), null)).thenReturn("Asia/Bangkok");

        List<RequestHistoryDto> actualResult = requestHistoryService.getRequestHistoryByRequestID(requestId);
        // act

        // assert
        assertNotNull(actualResult);
        assertEquals(1, actualResult.size());
        assertEquals(requestId, actualResult.get(0).getRecId());
        verify(requestHistoryRepository, times(1)).getRequestHistoryListByCondition(anyLong());

    }

    @Test
    public void testSaveRequestHistory() {
        // arrange
        RequestHistoryRequest requestHistoryRequest = new RequestHistoryRequest();
        requestHistoryRequest.setRecId(requestId);
        requestHistoryRequest.setRequestId(requestId);
        requestHistoryRequest.setTenantId(tenant.getRecId());
        requestHistoryRequest.setActivityId(activity.getRecId());

        when(requestRepository.getById(anyLong())).thenReturn(request);
        when(tenantRepository.getById(anyInt())).thenReturn(tenant);
        when(activityRepository.getById(anyInt())).thenReturn(activity);
        when(requestHistoryRepository.save(any(RequestHistory.class))).thenReturn(requestHistory);

        // act
        RequestHistory actual = requestHistoryService.saveRequestHistory(requestHistoryRequest);

        // assert
        assertNotNull(actual);
        assertEquals(requestId, actual.getRequest().getRecId());
        assertEquals(1, actual.getTenant().getRecId());
        assertEquals(1, actual.getActivity().getRecId());
        verify(requestRepository, times(1)).getById(anyLong());
        verify(tenantRepository, times(1)).getById(anyInt());
        verify(activityRepository, times(1)).getById(anyInt());
        verify(requestHistoryRepository, times(1)).save(any(RequestHistory.class));
    }

}
