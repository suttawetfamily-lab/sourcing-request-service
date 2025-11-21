package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.ApprovalSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestApproveRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestForwarderRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestRejectRequest;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestPurchaserService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApprovalWorkflowControllerTest {

    @Mock
    private RequestService requestService;

    @Mock
    private RequestPurchaserService requestPurchaserService;

    @InjectMocks
    private ApprovalWorkflowController controller;

    private RequestApproveRequest approveRequest;
    private RequestRejectRequest rejectRequest;
    private RequestForwarderRequest forwarderRequest;
    private ApprovalSearchRequest searchRequest;

    @Before
    public void setup() {
        approveRequest = new RequestApproveRequest();
        rejectRequest = new RequestRejectRequest();
        forwarderRequest = new RequestForwarderRequest();
        searchRequest = new ApprovalSearchRequest();
        searchRequest.setPage(1);
        searchRequest.setPageSize(10);
    }

    @Test
    public void approveRequest_Success() {
        RequestStatusDto requestStatusDto = RequestStatusDto.builder()
                .recId(1)
                .name("Approved")
                .build();

        RequestDto mockRequestDto = new RequestDto();
        mockRequestDto.setRecId(1L);
        mockRequestDto.setRequestStatus(requestStatusDto);

        when(requestService.approveRequest(any(RequestApproveRequest.class))).thenReturn(mockRequestDto);
        ResponseEntity response = controller.approveRequest(approveRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void approveRequest_WorkflowError() {
        when(requestService.approveRequest(any(RequestApproveRequest.class))).thenThrow(new RuntimeException("Workflow API error"));
        ResponseEntity response = controller.approveRequest(approveRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void rejectRequest_Success() {
        RequestStatusDto requestStatusDto = RequestStatusDto.builder()
                .recId(1)
                .name("Approved")
                .build();

        RequestDto mockRequestDto = new RequestDto();
        mockRequestDto.setRecId(1L);
        mockRequestDto.setRequestStatus(requestStatusDto);

        when(requestService.rejectRequest(any(RequestRejectRequest.class))).thenReturn(mockRequestDto);
        ResponseEntity response = controller.rejectRequest(rejectRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void searchAllApprovalRequest_Success() {
        ApproverRequestSearchDto mockDto = new ApproverRequestSearchDto();
        mockDto.setRequestDtoList(new ArrayList<>());
        when(requestService.searchAllApprovalListByCondition(any(), any(Pageable.class))).thenReturn(mockDto);

        ResponseEntity response = controller.searchAllApprovalRequest(searchRequest);
        assertNotNull(response);
    }

    @Test
    public void searchMyApprovalRequest_Success() {
        ApproverRequestSearchDto mockDto = new ApproverRequestSearchDto();
        mockDto.setRequestDtoList(new ArrayList<>());
        when(requestService.searchMyApprovalListByCondition(any(), any(Pageable.class))).thenReturn(mockDto);

        ResponseEntity response = controller.searchMyApprovalRequest(searchRequest);
        assertNotNull(response);
    }

    @Test
    public void forwardApproval_Success() {
        when(requestService.forwardApproval(any())).thenReturn(true);
        ResponseEntity<?> response = controller.forwardApproval(forwarderRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void forwardApproval_Failure() {
        when(requestService.forwardApproval(any())).thenReturn(false);
        ResponseEntity<?> response = controller.forwardApproval(forwarderRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getDefaultApproval_Success() {
        List<DefaultApprovalDto> mockList = new ArrayList<>();
        when(requestService.getDefaultApproval(anyString(), anyDouble())).thenReturn(mockList);

        ResponseEntity response = controller.getDefaultApproval("erfx123", 100.0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getDefaultApproval_WorkflowError() {
        when(requestService.getDefaultApproval(anyString(), anyDouble()))
            .thenThrow(new RuntimeException("Workflow API error"));

        ResponseEntity response = controller.getDefaultApproval("erfx123", 100.0);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}