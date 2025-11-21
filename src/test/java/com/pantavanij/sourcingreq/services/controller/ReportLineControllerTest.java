package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ReportLineService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportLineControllerTest {

    @Mock
    private ReportLineService reportLineService;

    @InjectMocks
    private ReportLineController reportLineController;

    private ReportLineDto mockReportLineDto;
    private ReportLineRequest mockReportLineRequest;
    private ReportLineSearchRequest mockSearchRequest;

    @Before
    public void setup() {
        mockReportLineDto = new ReportLineDto();
        mockReportLineDto.setRecId(1L);
        mockReportLineDto.setReportLineName("Test Report Line");

        mockReportLineRequest = new ReportLineRequest();
        mockReportLineRequest.setReportLineName("Test Report Line");

        mockSearchRequest = new ReportLineSearchRequest();
        mockSearchRequest.setPage(1);
        mockSearchRequest.setPageSize(10);
        mockSearchRequest.setSortBy("reportLineName");
        mockSearchRequest.setSortOrder("desc");
    }

    @Test
    public void getDefaultReportList_ShouldReturnDefaultResponse() {
        ResponseEntity<RequestDefaultReportLineResponse> response = reportLineController.getDefaultReportList();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
    }

    @Test
    public void reportLineView_WhenFound_ShouldReturnReportLine() {
        when(reportLineService.findReportLineByRecId(1)).thenReturn(mockReportLineDto);

        ResponseEntity response = reportLineController.reportLineView(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
    }

    @Test
    public void createReportLine_WhenSuccessful_ShouldReturnCreatedStatus() {
        when(reportLineService.createReportLine(any(ReportLineRequest.class))).thenReturn(mockReportLineDto);

        ResponseEntity response = reportLineController.createReportLine(mockReportLineRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
    }

    @Test
    public void reportLineSearch_WhenResultsFound_ShouldReturnSearchResults() {
        ReportLineSearchDto searchDto = new ReportLineSearchDto();
        searchDto.setReportLines(Arrays.asList(mockReportLineDto));
        searchDto.setTotal(1L);
        searchDto.setTotalPage(1);
        searchDto.setPageSize(10);

        when(reportLineService.searchReportLineListByConditions(any(), any())).thenReturn(searchDto);

        ResponseEntity response = reportLineController.reportLineSearch(mockSearchRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ReportLineSearchResponse);
    }

    @Test
    public void updateReportLine_WhenSuccessful_ShouldReturnUpdatedReportLine() {
        when(reportLineService.updateReportLine(any(ReportLineRequest.class))).thenReturn(mockReportLineDto);

        ResponseEntity response = reportLineController.updateReportLine(mockReportLineRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
    }

    @Test
    public void deleteReportLine_WhenSuccessful_ShouldReturnOkStatus() {
        when(reportLineService.deleteReportLine(1)).thenReturn(true);

        ResponseEntity response = reportLineController.deleteReportLine(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponseStatus);
    }

    @Test
    public void searchRequest_WhenResultsFound_ShouldReturnFilteredResults() {
        EPAuthReviewerResponse mockResponse = new EPAuthReviewerResponse();
        List<EPAuthReviewerDto> reviewerList = new ArrayList<>();
        EPAuthReviewerDto reviewer = new EPAuthReviewerDto();
        reviewer.setSysUserId(1);
        reviewerList.add(reviewer);
        mockResponse.setData(reviewerList);

        when(reportLineService.getReportLineListByConditions(any(), any())).thenReturn(mockResponse);

        ReportLineSearchRequest searchRequest = new ReportLineSearchRequest();
        searchRequest.setExceptReportLines(new ArrayList<>());

        ResponseEntity response = reportLineController.searchRequest(searchRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof EPAuthReviewerResponse);
    }

    @Test
    public void updateReportLineSequence_WhenSuccessful_ShouldReturnUpdatedSequence() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        when(reportLineService.updateReportLineSequence(any(SequenceRequest.class))).thenReturn(mockReportLineDto);

        ResponseEntity response = reportLineController.updateReportLineSequence(sequenceRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
    }
}
