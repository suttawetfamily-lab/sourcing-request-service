package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestForwarder;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestForwarderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestForwarderServiceImplTest {

    @InjectMocks
    private RequestForwarderServiceImpl requestForwarderService;
    @Mock
    private RequestForwarderRepository requestForwarderRepository;
    @Mock
    private TenantConfigServiceImpl tenantConfigService;


    @Test
    public void testIsCurrentForwarder_caseFoundData_shouldReturnTrue() {
        // arrange
        Long requestId = Long.valueOf(112233);
        String toApprovalName = "ApprovalName";
        when(requestForwarderRepository.getCurrentForwarderByRequestIdAndToApprovalName(anyLong(), anyString())).thenReturn(Optional.of(new RequestForwarder()));

        // act
        boolean isCurrentForwarder = requestForwarderService.isCurrentForwarder(requestId, toApprovalName);

        // assert
        verify(requestForwarderRepository, times(1)).getCurrentForwarderByRequestIdAndToApprovalName(requestId, toApprovalName);
        assertTrue(isCurrentForwarder);
    }

    @Test
    public void testIsCurrentForwarder_caseNotFoundData_shouldReturnFalse() {
        // arrange
        Long requestId = Long.valueOf(112233);
        String toApprovalName = "ApprovalName";
        when(requestForwarderRepository.getCurrentForwarderByRequestIdAndToApprovalName(anyLong(), anyString())).thenReturn(Optional.empty());

        // act
        boolean isCurrentForwarder = requestForwarderService.isCurrentForwarder(requestId, toApprovalName);

        // assert
        verify(requestForwarderRepository, times(1)).getCurrentForwarderByRequestIdAndToApprovalName(requestId, toApprovalName);
        assertFalse(isCurrentForwarder);
    }

    @Test
    public void testIsForwardApprovalWorkflow_caseCurrentForwarderIsTrueAndHavingForwardApprovalConfig_shouldReturnTrue() {
        // arrange
        Long requestId = Long.valueOf(112233);
        String assingBy = "AssignBy";
        String userLogin = "UserLogin";
        Integer tenantId = 1;

        when(requestForwarderRepository.getCurrentForwarderByRequestIdAndToApprovalName(anyLong(), anyString())).thenReturn(Optional.of(new RequestForwarder()));
        when(tenantConfigService.getForwardApprovalWorkflow(anyInt())).thenReturn(true);

        // act
        boolean isForwardApprovalWorkflow = requestForwarderService.isForwardApprovalWorkflow(requestId, assingBy, userLogin, tenantId);

        // assert
        assertTrue(isForwardApprovalWorkflow);
    }

    @Test
    public void testIsForwardApprovalWorkflow_caseUserLoginEqualAssignByAndHavingForwardApprovalConfig_shouldReturnTrue() {
        // arrange
        Long requestId = Long.valueOf(112233);
        String assingBy = "AssignBy";
        String userLogin = "AssignBy";
        Integer tenantId = 1;

        when(requestForwarderRepository.getCurrentForwarderByRequestIdAndToApprovalName(anyLong(), anyString())).thenReturn(Optional.empty());
        when(tenantConfigService.getForwardApprovalWorkflow(anyInt())).thenReturn(true);

        // act
        boolean isForwardApprovalWorkflow = requestForwarderService.isForwardApprovalWorkflow(requestId, assingBy, userLogin, tenantId);

        // assert
        assertTrue(isForwardApprovalWorkflow);
    }

    @Test
    public void testIsForwardApprovalWorkflow_caseUserLoginEqualAssignByAndNotFoundForwardApprovalConfig_shouldReturnFalse() {
        // arrange
        Long requestId = Long.valueOf(112233);
        String assingBy = "AssignBy";
        String userLogin = "AssignBy";
        Integer tenantId = 1;

        when(requestForwarderRepository.getCurrentForwarderByRequestIdAndToApprovalName(anyLong(), anyString())).thenReturn(Optional.of(new RequestForwarder()));
        when(tenantConfigService.getForwardApprovalWorkflow(anyInt())).thenReturn(false);

        // act
        boolean isForwardApprovalWorkflow = requestForwarderService.isForwardApprovalWorkflow(requestId, assingBy, userLogin, tenantId);

        // assert
        assertFalse(isForwardApprovalWorkflow);
    }

    @Test
    public void testIsForwardApprovalWorkflow_caseUserLoginNotEqualAssignByAndNotFoundForwardApprovalConfig_shouldReturnFalse() {
        // arrange
        Long requestId = Long.valueOf(112233);
        String assingBy = "AssignBy";
        String userLogin = "UserLogin";
        Integer tenantId = 1;

        when(requestForwarderRepository.getCurrentForwarderByRequestIdAndToApprovalName(anyLong(), anyString())).thenReturn(Optional.empty());

        // act
        boolean isForwardApprovalWorkflow = requestForwarderService.isForwardApprovalWorkflow(requestId, assingBy, userLogin, tenantId);

        // assert
        assertFalse(isForwardApprovalWorkflow);
    }
}
