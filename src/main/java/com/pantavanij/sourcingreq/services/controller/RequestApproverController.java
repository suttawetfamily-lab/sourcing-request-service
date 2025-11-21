package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.EPAuthReviewerDto;
import com.pantavanij.sourcingreq.services.domain.request.ApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthReviewerResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestPurchaserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class RequestApproverController {

//    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    private final RequestPurchaserService requestPurchaserService;

    @GetMapping(value = "/request-approver/init", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity initRequestApprover() {
        if (requestPurchaserService.initializeRequestApprover()) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1004, ApiMessage.I1004.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E1001, ApiMessage.E1001.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/request-approver/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequest(@RequestBody @Valid ApproverSearchRequest reviewerSearchRequest) {
        EPAuthReviewerResponse response = requestPurchaserService.getApproverListByConditions(reviewerSearchRequest);
        if (response.getData() != null && !response.getData().isEmpty()) {
            List<EPAuthReviewerDto> epAuthReviewerList =
                    response.getData().stream().filter(
                                    i -> !reviewerSearchRequest.getExceptApprovers().contains(i.getSysUserId().toString()))
                            .distinct()
                            .collect(Collectors.toList());
            response.setData(epAuthReviewerList);
            response.setTotal(epAuthReviewerList.size());
            return ResponseEntity.ok().body(response);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }
}
