package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.DelegatorByDelegateeRequest;
import com.pantavanij.sourcingreq.services.domain.response.DelegatorByDelegateeResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DelegationService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private DelegationService delegationService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Tenant mockTenant;
    private DelegatorByDelegateeResponse mockDelegationResponse;

    @Before
    public void setup() {
        mockTenant = new Tenant();
        mockTenant.setRecId(1);

        mockDelegationResponse = new DelegatorByDelegateeResponse();
        mockDelegationResponse.setDelegatorUserNames(List.of("delegator1"));

        when(tenantService.findByCode(anyString())).thenReturn(mockTenant);
        when(delegationService.getActiveDelegationByDeletatorAndDelegatee(any(DelegatorByDelegateeRequest.class))).thenReturn(mockDelegationResponse);
    }
    @Test
    public void getCountApproverTask_WhenDeptApprover_ShouldReturnCorrectCount() {
        try (MockedStatic<AppUtil> mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn("tenant1");
            mockedStatic.when(AppUtil::getUserName).thenReturn("user1");
            mockedStatic.when(AppUtil::isDeptApprover).thenReturn(true);
            mockedStatic.when(AppUtil::isPurchaser).thenReturn(false);

            when(requestRepository.countDeptApproverTask(any(), anyString(), any()))
                .thenReturn(2)  // for delegator
                .thenReturn(3); // for current user

            Integer result = notificationService.getCountApproverTask();
            assertEquals(Integer.valueOf(5), result);
        }
    }

    @Test
    public void getCountApproverTask_WhenPurchaser_ShouldReturnCorrectCount() {
        try (MockedStatic<AppUtil> mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn("tenant1");
            mockedStatic.when(AppUtil::getUserName).thenReturn("user1");
            mockedStatic.when(AppUtil::isDeptApprover).thenReturn(false);
            mockedStatic.when(AppUtil::isPurchaser).thenReturn(true);

            when(requestRepository.countPurchaserTask(any(), anyString(), any()))
                .thenReturn(1)  // for delegator
                .thenReturn(4); // for current user

            Integer result = notificationService.getCountApproverTask();
            assertEquals(Integer.valueOf(5), result);
        }
    }

    @Test
    public void getCountApproverTask_WhenNoDelegation_ShouldReturnOnlyUserCount() {
        mockDelegationResponse.setDelegatorUserNames(Collections.emptyList());

        try (MockedStatic<AppUtil> mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn("tenant1");
            mockedStatic.when(AppUtil::getUserName).thenReturn("user1");
            mockedStatic.when(AppUtil::isDeptApprover).thenReturn(true);
            mockedStatic.when(AppUtil::isPurchaser).thenReturn(false);

            when(requestRepository.countDeptApproverTask(any(), anyString(), any()))
                .thenReturn(3); // for current user

            Integer result = notificationService.getCountApproverTask();
            assertEquals(Integer.valueOf(3), result);
        }
    }

    @Test
    public void getCountApproverTask_WhenException_ShouldReturnMinusOne() {
        try (MockedStatic<AppUtil> mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn("tenant1");
            mockedStatic.when(AppUtil::getUserName).thenReturn("user1");
            mockedStatic.when(AppUtil::isDeptApprover).thenReturn(true);

            when(requestRepository.countDeptApproverTask(any(), anyString(), any()))
                .thenThrow(new RuntimeException("Test exception"));

            Integer result = notificationService.getCountApproverTask();
            assertEquals(Integer.valueOf(-1), result);
        }
    }

    @Test
    public void getCountReviewerTask_ShouldReturnCorrectCount() {
        try (MockedStatic<AppUtil> mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn("tenant1");
            mockedStatic.when(AppUtil::getUserName).thenReturn("user1");

            when(requestRepository.countReviewerAndReportLineTask(any(), anyString()))
                .thenReturn(5);

            Integer result = notificationService.getCountReviewerTask();
            assertEquals(Integer.valueOf(5), result);
        }
    }

    @Test
    public void getCountReviewerTask_WhenException_ShouldReturnMinusOne() {
        try (MockedStatic<AppUtil> mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn("tenant1");
            mockedStatic.when(AppUtil::getUserName).thenReturn("user1");

            when(requestRepository.countReviewerAndReportLineTask(any(), anyString()))
                .thenThrow(new RuntimeException("Test exception"));

            Integer result = notificationService.getCountReviewerTask();
            assertEquals(Integer.valueOf(-1), result);
        }
    }
}
