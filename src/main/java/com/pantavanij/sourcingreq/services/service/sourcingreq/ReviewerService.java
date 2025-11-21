package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.EPAuthReviewerDto;
import com.pantavanij.sourcingreq.services.domain.dto.ReviewerDto;
import com.pantavanij.sourcingreq.services.domain.dto.ReviewerSearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Reviewer;
import com.pantavanij.sourcingreq.services.domain.request.ReviewerRequest;
import com.pantavanij.sourcingreq.services.domain.request.ReviewerSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthReviewerResponse;
import com.pantavanij.sourcingreq.services.domain.response.ReviewerResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewerService {
    ReviewerResponse getRequestReviewer(ReviewerSearchRequest request, Pageable pageable);

    Integer saveReviewer(ReviewerDto reviewerDto);

    EPAuthReviewerResponse getReviewerListByConditions(ReviewerSearchRequest request);

    ReviewerSearchDto searchReviewerListByConditions(ReviewerSearchRequest request, Pageable pageable);

    ReviewerDto findReviewerByRecId(Integer reviewerId);

    List<Reviewer> findByReviewerName(String reviewerName);

    ReviewerDto createReviewer(ReviewerRequest reviewerRequest);

    ReviewerDto updateReviewer(ReviewerRequest reviewerRequest);

    boolean deleteReviewer(Integer reviewerId);

    ReviewerDto updateReviewerSequence(SequenceRequest request);
}
