package com.pantavanij.sourcingreq.services.controller;

import com.github.javafaker.Faker;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ReviewerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewerControllerTest {

    @Mock
    private ReviewerService reviewerService;

    @InjectMocks
    private ReviewerController reviewerController;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @Test
    void reviewerView_Success() {
        // Arrange
        Long reviewerId = faker.number().randomNumber();
        ReviewerDto mockReviewerDto = new ReviewerDto();
        mockReviewerDto.setRecId(reviewerId);
        mockReviewerDto.setReviewerName(faker.name().fullName());

        when(reviewerService.findReviewerByRecId(reviewerId.intValue())).thenReturn(mockReviewerDto);

        // Act
        ResponseEntity response = reviewerController.reviewerView(reviewerId.intValue());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void searchRequest_Success() {
        // Arrange
        ReviewerSearchRequest searchRequest = new ReviewerSearchRequest();
        searchRequest.setExceptReviewers(Collections.emptyList());

        EPAuthReviewerDto reviewerDto = new EPAuthReviewerDto();
        reviewerDto.setSysUserId(faker.number().randomDigit());

        EPAuthReviewerResponse mockResponse = new EPAuthReviewerResponse();
        mockResponse.setData(Arrays.asList(reviewerDto));

        when(reviewerService.getReviewerListByConditions(any())).thenReturn(mockResponse);

        // Act
        ResponseEntity response = reviewerController.searchRequest(searchRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createReviewer_Success() {
        // Arrange
        ReviewerRequest request = new ReviewerRequest();
        request.setReviewerName(faker.name().fullName());

        ReviewerDto mockReviewerDto = new ReviewerDto();
        mockReviewerDto.setRecId(0L);
        mockReviewerDto.setReviewerName(request.getReviewerName());

        when(reviewerService.createReviewer(any())).thenReturn(mockReviewerDto);

        // Act
        ResponseEntity response = reviewerController.createReviewer(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void reviewerSearch_Success() {
        // Arrange
        ReviewerSearchRequest searchRequest = new ReviewerSearchRequest();
        searchRequest.setPage(1);
        searchRequest.setPageSize(10);

        ReviewerDto reviewerDto = new ReviewerDto();
        reviewerDto.setRecId(faker.number().randomNumber());
        reviewerDto.setReviewerName(faker.name().fullName());

        ReviewerSearchDto mockSearchDto = ReviewerSearchDto.builder()
                .reviewers(List.of(reviewerDto))
                .total(1L)
                .totalPage(1)
                .pageSize(10)
                .build();

        when(reviewerService.searchReviewerListByConditions(any(), any())).thenReturn(mockSearchDto);

        // Act
        ResponseEntity response = reviewerController.reviewerSearch(searchRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void updateReviewer_Success() {
        // Arrange
        ReviewerRequest request = new ReviewerRequest();
        request.setRecId(faker.number().randomDigit());
        request.setReviewerName(faker.name().fullName());

        ReviewerDto mockReviewerDto = new ReviewerDto();
        mockReviewerDto.setRecId(faker.number().randomNumber());
        mockReviewerDto.setReviewerName(faker.name().fullName());

        when(reviewerService.updateReviewer(any())).thenReturn(mockReviewerDto);

        // Act
        ResponseEntity response = reviewerController.updateReviewer(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void updateReviewerSequence_Success() {
        // Arrange
        SequenceRequest request = new SequenceRequest();
        request.setRecId(faker.number().randomDigit());

        ReviewerDto mockReviewerDto = new ReviewerDto();
        mockReviewerDto.setRecId(faker.number().randomNumber());
        mockReviewerDto.setSequence(1);

        when(reviewerService.updateReviewerSequence(any())).thenReturn(mockReviewerDto);

        // Act
        ResponseEntity response = reviewerController.updateReviewerSequence(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void deleteReviewer_Success() {
        // Arrange
        Integer reviewerId = faker.number().randomDigit();
        when(reviewerService.deleteReviewer(reviewerId)).thenReturn(true);

        // Act
        ResponseEntity response = reviewerController.deleteReviewer(reviewerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void deleteReviewer_Failure() {
        // Arrange
        Integer reviewerId = faker.number().randomDigit();
        when(reviewerService.deleteReviewer(reviewerId)).thenReturn(false);

        // Act
        ResponseEntity response = reviewerController.deleteReviewer(reviewerId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
