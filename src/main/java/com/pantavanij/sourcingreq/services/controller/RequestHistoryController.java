package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.RequestHistoryDto;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class RequestHistoryController {
    private final RequestHistoryService requestHistoryService;

//    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL')")
//    @GetMapping(value = "/request-history/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getRequestHistoryByRequestID(@PathVariable Long requestId) {
//        List<RequestHistoryDto> requestHistoryDtoList = requestHistoryService.getRequestHistoryByRequestID(requestId);
//        if (requestHistoryDtoList != null && !requestHistoryDtoList.isEmpty()) {
//            return ResponseEntity.ok().body(requestHistoryDtoList);
//        } else if (requestHistoryDtoList == null) {
//            return new ResponseEntity<>(new ApiResponse( requestHistoryDtoList, new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description())), HttpStatus.METHOD_NOT_ALLOWED);
//        } else {
//            return new ResponseEntity<>(new ApiResponse(requestHistoryDtoList, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())), HttpStatus.OK);
//        }
//    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA','SQX','SQE')")
    @GetMapping(value = "/request-history-all/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllRequestHistoryByRequestID(@PathVariable Long requestId) {
        List<RequestHistoryDto> requestHistoryDtoList = requestHistoryService.getAllRequestHistoryByRequestID(requestId);
        if (requestHistoryDtoList != null && !requestHistoryDtoList.isEmpty()) {
            return ResponseEntity.ok().body(requestHistoryDtoList);
        } else if (requestHistoryDtoList == null){
            return new ResponseEntity<>(new ApiResponse( requestHistoryDtoList, new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description())), HttpStatus.METHOD_NOT_ALLOWED);
        } else {
            return new ResponseEntity<>(new ApiResponse(requestHistoryDtoList, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())), HttpStatus.OK);
        }
    }

//    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
//    @PostMapping(value = "/request-history", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity addRequestHistory(@RequestBody RequestHistoryRequest requestHistoryRequest) {
//        RequestHistory requestHistory = requestHistoryService.saveRequestHistory(requestHistoryRequest);
//        if (requestHistory != null) {
//            return new ResponseEntity<>(new RequestHistoryResponse(requestHistory), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

}
