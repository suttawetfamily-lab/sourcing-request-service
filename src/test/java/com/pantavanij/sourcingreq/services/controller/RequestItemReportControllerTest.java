package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemReportSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemReportService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestItemReportControllerTest {

    @Mock
    private RequestItemReportService requestItemReportService;

    @InjectMocks
    private RequestItemReportController requestItemReportController;

    private RequestItemReportDto mockReportDto;
    private RequestItemReportRequest mockRequest;
    private RequestItemReportSearchRequest mockSearchRequest;
    private RequestItemReportSearchDto mockSearchDto;

    @Before
    public void setup() {
        mockReportDto = new RequestItemReportDto();
        mockRequest = new RequestItemReportRequest();
        mockSearchRequest = new RequestItemReportSearchRequest();
        mockSearchRequest.setPage(1);
        mockSearchRequest.setPageSize(10);

        mockSearchDto = new RequestItemReportSearchDto();
        List<RequestItemReportDto> reportList = new ArrayList<>();
        reportList.add(mockReportDto);
        mockSearchDto.setRequestItemReportDtoList(reportList);
        mockSearchDto.setTotal(1L);
        mockSearchDto.setTotalPage(1);
    }

    @Test
    public void viewRequestItemReport_Success() {
        when(requestItemReportService.findRequestItemReportByRecId(1)).thenReturn(mockReportDto);

        ResponseEntity response = requestItemReportController.viewRequestItemReport(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void viewRequestItemReport_NotFound() {
        when(requestItemReportService.findRequestItemReportByRecId(1)).thenReturn(null);

        ResponseEntity response = requestItemReportController.viewRequestItemReport(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createRequestItemReport_Success() {
        when(requestItemReportService.createRequestItemReport(any(RequestItemReportRequest.class),0))
                .thenReturn(mockReportDto);

        ResponseEntity response = requestItemReportController.createRequestItemReport(mockRequest,0);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void searchRequestItemReport_Success() {
        when(requestItemReportService.searchRequestItemReportListByCondition(
                any(RequestItemReportSearchRequest.class), any(Pageable.class), 0))
                .thenReturn(mockSearchDto);

        ResponseEntity response = requestItemReportController.searchRequestItemReport(mockSearchRequest, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void updateRequestItemReport_Success() {
        when(requestItemReportService.updateRequestItemReport(any(RequestItemReportRequest.class), 0))
                .thenReturn(mockReportDto);

        ResponseEntity response = requestItemReportController.updateRequestItemReport(mockRequest, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void updateRequestItemReportSequence_Success() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        when(requestItemReportService.updateRequestItemReportSequence(any(SequenceRequest.class)))
                .thenReturn(mockReportDto);

        ResponseEntity response = requestItemReportController.updateRequestItemReportSequence(sequenceRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void deleteReportLine_Success() {
        when(requestItemReportService.deleteRequestItemReport(1)).thenReturn(true);

        ResponseEntity response = requestItemReportController.deleteReportLine(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void deleteReportLine_Failure() {
        when(requestItemReportService.deleteRequestItemReport(1)).thenReturn(false);

        ResponseEntity response = requestItemReportController.deleteReportLine(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
