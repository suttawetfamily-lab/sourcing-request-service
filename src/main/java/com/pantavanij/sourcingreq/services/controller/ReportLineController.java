package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ReportLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.SearchReportLine.*;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.REQUEST_NO;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class ReportLineController {

    private final ReportLineService reportLineService;

    @GetMapping(value = "/request-reportLine", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDefaultReportLineResponse> getDefaultReportList() {
        EPAuthReviewerDto defaultReportLine  = new EPAuthReviewerDto();
        RequestDefaultReportLineResponse response = RequestDefaultReportLineResponse.builder()
                .data(defaultReportLine)
                .status(new ApiResponseStatus())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasAuthority('SMR')")
    @GetMapping(value = "/report-line/view/{reportLineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reportLineView(@PathVariable ("reportLineId") Integer reportLineId) {
        ReportLineDto reportLineDto = reportLineService.findReportLineByRecId(reportLineId);
        if (reportLineDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(reportLineDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @ControllerExecuteTime
    @PostMapping(value = "/request-report-line/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequest(@RequestBody @Valid ReportLineSearchRequest requestSearchRequest) {
        EPAuthReviewerResponse requestReportLineResponse = reportLineService.getReportLineListByConditions(requestSearchRequest, null);
        if (requestReportLineResponse.getData() != null && !requestReportLineResponse.getData().isEmpty()) {
            List<EPAuthReviewerDto> epAuthReviewerList =
                    requestReportLineResponse.getData().stream().filter(
                                    i -> !requestSearchRequest.getExceptReportLines().contains(i.getSysUserId()))
                            .distinct()
                            .collect(Collectors.toList());
            requestReportLineResponse.setData(epAuthReviewerList);
            requestReportLineResponse.setTotal(epAuthReviewerList.size());
            return ResponseEntity.ok().body(requestReportLineResponse);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMR')")
    @PostMapping(value = "/report-line/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createReportLine(@RequestBody @Valid ReportLineRequest reportLineRequest) {
        ReportLineDto reportLineDto = reportLineService.createReportLine(reportLineRequest);
        if (reportLineDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(reportLineDto, new ApiResponseStatus()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMR')")
    @PostMapping(value = "/report-line/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reportLineSearch(@RequestBody @Valid ReportLineSearchRequest requestSearchRequest) {
        Sort sort;
        int page = requestSearchRequest.getPage();
        int size = requestSearchRequest.getPageSize();
        String sortBy = requestSearchRequest.getSortBy();
        String sortOrder = requestSearchRequest.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by("reportLineName").descending();
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
        ReportLineSearchDto reportLineSearchDto = reportLineService.searchReportLineListByConditions(requestSearchRequest, pageable);
        ReportLineSearchResponse response = ReportLineSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(reportLineSearchDto.getPageSize())
                .page(page)
                .total(reportLineSearchDto.getTotal())
                .totalPage(reportLineSearchDto.getTotalPage())
                .data(reportLineSearchDto.getReportLines())
                .build();

        if (reportLineSearchDto.getReportLines() != null && !reportLineSearchDto.getReportLines().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMR')")
    @PutMapping(value = "/report-line/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateReportLine(@RequestBody @Valid ReportLineRequest reportLineRequest) {
        ReportLineDto reportLineDto = reportLineService.updateReportLine(reportLineRequest);
        if (reportLineDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(reportLineDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMR')")
    @PutMapping(value = "/report-line/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateReportLineSequence(@RequestBody @Valid SequenceRequest request) {
        ReportLineDto reportLineDto = reportLineService.updateReportLineSequence(request);
        if (reportLineDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(reportLineDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMR')")
    @DeleteMapping(value = "/report-line/{reportLineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteReportLine(@PathVariable("reportLineId") Integer reportLineId) {
        boolean isDeleted = reportLineService.deleteReportLine(reportLineId);
        if (isDeleted) {
            return new ResponseEntity<>(new ApiResponseStatus(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
