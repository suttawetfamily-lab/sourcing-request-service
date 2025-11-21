package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ReviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;
import static com.pantavanij.sourcingreq.services.enums.SearchReviewer.REVIEWER_NAME;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class ReviewerController {

    private final ReviewerService reviewerService;

    @PreAuthorize("hasAuthority('SMV')")
    @GetMapping(value = "/reviewer/view/{reviewerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reviewerView(@PathVariable ("reviewerId") Integer reviewerId) {
        ReviewerDto reviewerDto = reviewerService.findReviewerByRecId(reviewerId);
        if (reviewerDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(reviewerDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @ControllerExecuteTime
    @PostMapping(value = "/request-reviewer/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequest(@RequestBody @Valid ReviewerSearchRequest requestSearchRequest) {
        EPAuthReviewerResponse requestReviewerResponse = reviewerService.getReviewerListByConditions(requestSearchRequest);
        if (requestReviewerResponse.getData() != null && !requestReviewerResponse.getData().isEmpty()) {
            List<EPAuthReviewerDto> epAuthReviewerList =
                    requestReviewerResponse.getData().stream().filter(
                                    i -> !requestSearchRequest.getExceptReviewers().contains(i.getSysUserId()))
                            .distinct()
                            .collect(Collectors.toList());
            requestReviewerResponse.setData(epAuthReviewerList);
            requestReviewerResponse.setTotal(epAuthReviewerList.size());
            return ResponseEntity.ok().body(requestReviewerResponse);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMV')")
    @PostMapping(value = "/reviewer/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createReviewer(@RequestBody @Valid ReviewerRequest reviewerRequest) {
        ReviewerDto reviewerDto = reviewerService.createReviewer(reviewerRequest);
        if (reviewerDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(reviewerDto, new ApiResponseStatus()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMV')")
    @PostMapping(value = "/reviewer/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reviewerSearch(@RequestBody @Valid ReviewerSearchRequest requestSearchRequest) {
        Sort sort;
        int page = requestSearchRequest.getPage();
        int size = requestSearchRequest.getPageSize();
        String sortBy = requestSearchRequest.getSortBy();
        String sortOrder = requestSearchRequest.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(REVIEWER_NAME.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortBy != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            if (!sortBy.equalsIgnoreCase("sequence")) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, SEQUENCE.description()));
            }
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        ReviewerSearchDto reviewerSearchDto = reviewerService.searchReviewerListByConditions(requestSearchRequest, pageable);
        ReviewerSearchResponse response = ReviewerSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(reviewerSearchDto.getPageSize())
                .page(page)
                .total(reviewerSearchDto.getTotal())
                .totalPage(reviewerSearchDto.getTotalPage())
                .data(reviewerSearchDto.getReviewers())
                .build();

        if (reviewerSearchDto.getReviewers() != null && !reviewerSearchDto.getReviewers().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMV')")
    @PutMapping(value = "/reviewer/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateReviewer(@RequestBody @Valid ReviewerRequest reviewerRequest) {
        ReviewerDto reviewerDto = reviewerService.updateReviewer(reviewerRequest);
        if (reviewerDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(reviewerDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMV')")
    @PutMapping(value = "/reviewer/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateReviewerSequence(@RequestBody @Valid SequenceRequest request) {
        ReviewerDto reviewerDto = reviewerService.updateReviewerSequence(request);
        if (reviewerDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(reviewerDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMV')")
    @DeleteMapping(value = "/reviewer/{reviewerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteReviewer(@PathVariable("reviewerId") Integer reviewerId) {
        boolean isDeleted = reviewerService.deleteReviewer(reviewerId);
        if (isDeleted) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
