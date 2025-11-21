package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.EPAuthRequesterDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequesterDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequesterSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequesterRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequesterSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import static com.pantavanij.sourcingreq.services.enums.SearchRequester.REQUESTER_NAME;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class RequesterController {

    private final RequesterService requesterService;

    @PreAuthorize("hasAuthority('SMM')")
    @GetMapping(value = "/requester/view/{requesterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity requesterView(@PathVariable ("requesterId") Integer requesterId) {
        RequesterDto requesterDto = requesterService.findRequesterByRecId(requesterId);
        if (requesterDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requesterDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @ControllerExecuteTime
    @PostMapping(value = "/request-requester/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequest(@RequestBody @Valid RequesterSearchRequest requestSearchRequest) {
        EPAuthRequesterResponse requestRequesterResponse = requesterService.getRequesterListByConditions(requestSearchRequest);
        if (requestRequesterResponse.getData() != null && !requestRequesterResponse.getData().isEmpty()) {
            List<EPAuthRequesterDto> epAuthRequesterList =
                    requestRequesterResponse.getData().stream().filter(
                                    i -> !requestSearchRequest.getExceptRequesters().contains(i.getSysUserId()))
                            .distinct()
                            .collect(Collectors.toList());
            requestRequesterResponse.setData(epAuthRequesterList);
            requestRequesterResponse.setTotal(epAuthRequesterList.size());
            return ResponseEntity.ok().body(requestRequesterResponse);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMM')")
    @PostMapping(value = "/requester/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createRequester(@RequestBody @Valid RequesterRequest requesterRequest) {
        RequesterDto requesterDto = requesterService.createRequester(requesterRequest);
        if (requesterDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requesterDto, new ApiResponseStatus()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMM')")
    @PostMapping(value = "/requester/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity requesterSearch(@RequestBody @Valid RequesterSearchRequest requestSearchRequest) {
        Sort sort;
        int page = requestSearchRequest.getPage();
        int size = requestSearchRequest.getPageSize();
        String sortBy = requestSearchRequest.getSortBy();
        String sortOrder = requestSearchRequest.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(REQUESTER_NAME.description()).descending();
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
        RequesterSearchDto requesterSearchDto = requesterService.searchRequesterListByConditions(requestSearchRequest, pageable);
        RequesterSearchResponse response = RequesterSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(requesterSearchDto.getPageSize())
                .page(page)
                .total(requesterSearchDto.getTotal())
                .totalPage(requesterSearchDto.getTotalPage())
                .data(requesterSearchDto.getRequesters())
                .build();

        if (requesterSearchDto.getRequesters() != null && !requesterSearchDto.getRequesters().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMM')")
    @PutMapping(value = "/requester/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequester(@RequestBody @Valid RequesterRequest requesterRequest) {
        RequesterDto requesterDto = requesterService.updateRequester(requesterRequest);
        if (requesterDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requesterDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMM')")
    @PutMapping(value = "/requester/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequesterSequence(@RequestBody @Valid SequenceRequest request) {
        RequesterDto requesterDto = requesterService.updateRequesterSequence(request);
        if (requesterDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requesterDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMM')")
    @DeleteMapping(value = "/requester/{requesterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteRequester(@PathVariable("requesterId") Integer requesterId) {
        boolean isDeleted = requesterService.deleteRequester(requesterId);
        if (isDeleted) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
