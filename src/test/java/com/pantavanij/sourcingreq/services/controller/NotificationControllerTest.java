package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.service.sourcingreq.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Before
    public void setup() {
        // Any setup if needed
    }

    @Test
    public void test_ShouldReturnHelloMessage() {
        ResponseEntity response = notificationController.test();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals("Hello, I'm from notification.", apiResponse.getData());
    }

    @Test
    public void countDepApproveTask_WhenSuccess_ShouldReturnCount() {
        // Given
        Integer expectedCount = 5;
        when(notificationService.getCountApproverTask()).thenReturn(expectedCount);

        // When
        ResponseEntity response = notificationController.countDepApproveTask();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals(expectedCount, apiResponse.getData());
    }

    @Test
    public void countDepApproveTask_WhenError_ShouldReturnInternalServerError() {
        // Given
        when(notificationService.getCountApproverTask()).thenReturn(-1);

        // When
        ResponseEntity response = notificationController.countDepApproveTask();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void countReviewerTask_WhenSuccess_ShouldReturnCount() {
        // Given
        Integer expectedCount = 3;
        when(notificationService.getCountReviewerTask()).thenReturn(expectedCount);

        // When
        ResponseEntity response = notificationController.countReviewerTask();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals(expectedCount, apiResponse.getData());
    }

    @Test
    public void countReviewerTask_WhenError_ShouldReturnInternalServerError() {
        // Given
        when(notificationService.getCountReviewerTask()).thenReturn(-1);

        // When
        ResponseEntity response = notificationController.countReviewerTask();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
