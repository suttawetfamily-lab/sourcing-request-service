package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.EPAuthReviewerDto;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.RequestReportLineDto;
import com.pantavanij.sourcingreq.services.domain.request.ReportLineCommentRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.RequestReportLineCommentResponse;
import com.pantavanij.sourcingreq.services.domain.response.RequestReportLineResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestReportLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class RequestReportLineController {

    private final RequestReportLineService requestReportLineService;

    @GetMapping(value = "/request-reportLine/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRequestReportLine(@PathVariable Long requestId) {
        List<EPAuthReviewerDto> reportLineDtoList = requestReportLineService.getByRequest(requestId);
        if (reportLineDtoList != null && !reportLineDtoList.isEmpty()) {
            RequestReportLineResponse response = RequestReportLineResponse.builder()
                    .status(new ApiResponseStatus())
                    .data(reportLineDtoList)
                    .build();
            return ResponseEntity.ok().body(response);
        } else if (reportLineDtoList == null) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
        } else {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }
    @PostMapping(value = "/request-reportLine/comment")
    public ResponseEntity addCommentRequestReviewer(@Valid @RequestBody ReportLineCommentRequest request) {
        List<RequestReportLineDto> requestReportLineDtos = requestReportLineService.saveComment(request);
        if(requestReportLineDtos != null && !requestReportLineDtos.isEmpty()) {
            return new ResponseEntity<>(new RequestReportLineCommentResponse(requestReportLineDtos), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()) , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
