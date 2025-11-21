package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.ApproverDto;
import com.pantavanij.sourcingreq.services.domain.dto.ApproverSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.ApproverRequest;
import com.pantavanij.sourcingreq.services.domain.request.ApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ApproverService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApproverControllerTest {

    @Mock
    private ApproverService approverService;

    @InjectMocks
    private ApproverController approverController;

    private ApproverDto mockApproverDto;
    private ApproverRequest mockApproverRequest;
    private ApproverSearchRequest mockSearchRequest;

    @Before
    public void setup() {
        mockApproverDto = new ApproverDto();
        mockApproverDto.setRecId(1);
        mockApproverDto.setApproverName("Test Approver");

        mockApproverRequest = new ApproverRequest();
        mockApproverRequest.setApproverName("Test Approver");

        mockSearchRequest = new ApproverSearchRequest();
        mockSearchRequest.setPage(1);
        mockSearchRequest.setPageSize(10);
        mockSearchRequest.setSortBy("approverName");
        mockSearchRequest.setSortOrder("desc");
    }

    @Test
    public void viewApprover_Success() {
        when(approverService.findApproverByRecId(1)).thenReturn(mockApproverDto);

        ResponseEntity response = approverController.viewApprover(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void viewApprover_NotFound() {
        when(approverService.findApproverByRecId(1)).thenReturn(null);

        ResponseEntity response = approverController.viewApprover(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createApprover_Success() {
        when(approverService.createApprover(any(ApproverRequest.class))).thenReturn(mockApproverDto);

        ResponseEntity response = approverController.createApprover(mockApproverRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void searchApprover_Success() {
        ApproverSearchDto searchDto = new ApproverSearchDto();
        List<ApproverDto> approverList = new ArrayList<>();
        approverList.add(mockApproverDto);
        searchDto.setApproverDtoList(approverList);
        searchDto.setTotal(1L);
        searchDto.setTotalPage(1);

        when(approverService.searchApproverListByCondition(eq(mockSearchRequest), any(Pageable.class)))
                .thenReturn(searchDto);

        ResponseEntity response = approverController.searchApprover(mockSearchRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void updateApprover_Success() {
        when(approverService.updateApprover(any(ApproverRequest.class))).thenReturn(mockApproverDto);

        ResponseEntity response = approverController.updateApprover(mockApproverRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void updateApproverSequence_Success() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        when(approverService.updateApproverSequence(any(SequenceRequest.class))).thenReturn(mockApproverDto);

        ResponseEntity response = approverController.updateApproverSequence(sequenceRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void deleteApprover_Success() {
        when(approverService.deleteApprover(1)).thenReturn(true);

        ResponseEntity response = approverController.deleteReportLine(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void deleteApprover_Failure() {
        when(approverService.deleteApprover(1)).thenReturn(false);

        ResponseEntity response = approverController.deleteReportLine(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
