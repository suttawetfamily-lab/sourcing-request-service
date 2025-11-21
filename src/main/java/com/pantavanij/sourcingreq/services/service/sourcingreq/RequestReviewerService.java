package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestReviewerDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestReviewerRequest;
import com.pantavanij.sourcingreq.services.domain.request.ReviewerCommentRequest;
import com.pantavanij.sourcingreq.services.domain.request.ReviewerSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthReviewerResponse;

import java.util.List;

public interface RequestReviewerService {
    List<RequestReviewerDto> saveRequestReviewer(RequestReviewerRequest requestReviewerRequest, EPAuthReviewerResponse response);
    List<RequestReviewerDto> findByRequest(Long requestId);
    List<RequestReviewerDto> saveCommentReviewer(ReviewerCommentRequest reviewerCommentRequest);
//    List<Long> findRequestReviewerByReviewerName(String reviewerName);
    EPAuthReviewerResponse getReviewerListByConditions(ReviewerSearchRequest request, String[] privilegeCodes);
    void deleteReviewerByRequestId(Long requestId);
}
