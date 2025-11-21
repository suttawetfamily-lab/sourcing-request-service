package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.ERFXBidDocResponse;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ERFXService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ERFXControllerTest {

    @Mock
    private ERFXService erfxService;

    @InjectMocks
    private ERFXController erfxController;

    private ERFXCreateRequest erfxCreateRequest;
    private ERFXReceiveRequest erfxReceiveRequest;
    private ERFXUpdateInfoRequest erfxUpdateInfoRequest;
    private ERFXCancelRequest erfxCancelRequest;

    @Before
    public void setup() {
        erfxCreateRequest = new ERFXCreateRequest();
        erfxCreateRequest.setDocNum("DOC123");
        erfxCreateRequest.setItems(new ArrayList<>());
        erfxCreateRequest.setAttachments(new ArrayList<>());

        erfxReceiveRequest = new ERFXReceiveRequest();
        erfxUpdateInfoRequest = new ERFXUpdateInfoRequest();
        erfxCancelRequest = new ERFXCancelRequest();
    }

    @Test
    public void createERFX_Success() {
        when(erfxService.createERFX(any(ERFXCreateRequest.class), anyString()))
            .thenReturn("http://redirect.url");

        ResponseEntity<?> response = erfxController.createERFX("authCode", erfxCreateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void cancelERFX_Success() {
        when(erfxService.cancelERFX(any(Long.class), any(ERFXCancelRequest.class)))
            .thenReturn(true);

        ResponseEntity response = erfxController.cancelERFX(1L, erfxCancelRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void receiveERFX_Success() throws Exception {
        when(erfxService.receiveERFX(anyString(), any(ERFXReceiveRequest.class)))
            .thenReturn(true);

        ResponseEntity response = erfxController.receiveERFX("ERFX123", erfxReceiveRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateERFXInfo_Success() throws Exception {
        when(erfxService.updateERFXInfo(any(ERFXUpdateInfoRequest.class)))
            .thenReturn(true);

        ResponseEntity response = erfxController.updateERFXInfo(erfxUpdateInfoRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getCreateNewURL_Success() {
        when(erfxService.getCreateNewURL(anyString()))
            .thenReturn("http://create.new.url");

        ResponseEntity response = erfxController.getCreateNewURL("authCode");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getCheckStatusURL_Success() {
        when(erfxService.getCheckStatusURL(anyString()))
            .thenReturn("http://check.status.url");

        ResponseEntity response = erfxController.getCheckStatusURL("authCode");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getApproveShortlistURL_Success() {
        when(erfxService.getApproveShortlistURL(anyString()))
            .thenReturn("http://approve.shortlist.url");

        ResponseEntity response = erfxController.getApproveShortlistURL("authCode");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getBidDoc_Success() {
        ERFXBidDocResponse mockResponse = new ERFXBidDocResponse();
        when(erfxService.getBidDoc(anyString()))
            .thenReturn(mockResponse);

        ResponseEntity response = erfxController.getBidDoc("DOC123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void createERFX_ExceedAttachmentLimit() {
        List<ERFXAttachmentDto> erfxAttachmentList = new ArrayList<>();
        // Add more attachments than allowed
        for (int i = 0; i < 100; i++) {
            erfxAttachmentList.add(new ERFXAttachmentDto());
        }
        erfxCreateRequest.setAttachments(erfxAttachmentList);

        ResponseEntity<?> response = erfxController.createERFX("authCode", erfxCreateRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void createERFX_ServiceFailure() {
        when(erfxService.createERFX(any(ERFXCreateRequest.class), anyString()))
            .thenReturn("");

        ResponseEntity<?> response = erfxController.createERFX("authCode", erfxCreateRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
