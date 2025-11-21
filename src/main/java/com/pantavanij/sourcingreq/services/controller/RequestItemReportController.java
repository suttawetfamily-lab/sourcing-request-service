package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemReportSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.RequestItemReportSearchResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.SearchRequestItemReportType;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemReportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.version}")
public class RequestItemReportController {

    private final RequestItemReportService requestItemReportService;

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/request-item-report/view/{requestItemReportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewRequestItemReport(@PathVariable("requestItemReportId") Integer requestItemReportId) {
        RequestItemReportDto dto = requestItemReportService.findRequestItemReportByRecId(requestItemReportId);
        if (dto != null) {
            return new ResponseEntity<>(new ApiResponse<>(dto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/request-item-report/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createRequestItemReport(
            @RequestBody @Valid RequestItemReportRequest requestItemReportRequest,
            @RequestHeader(value = "organization", required = false) Integer organizationId
    ) {
        RequestItemReportDto dto = requestItemReportService.createRequestItemReport(requestItemReportRequest, organizationId);
        if (dto != null) {
            return new ResponseEntity<>(new ApiResponse<>(dto, new ApiResponseStatus()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/request-item-report/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequestItemReport(
            @RequestBody @Valid RequestItemReportSearchRequest requestItemReportSearchRequest,
            @RequestHeader(value = "organization", required = false) Integer organizationId
    ) {
        Sort sort;
        int page = requestItemReportSearchRequest.getPage();
        int size = requestItemReportSearchRequest.getPageSize();
        String sortBy = requestItemReportSearchRequest.getSortBy();
        String sortOrder = requestItemReportSearchRequest.getSortOrder();

        if ((StringUtils.isEmpty(sortBy)) && (StringUtils.isEmpty(sortOrder))) {
            sort = Sort.by(SearchRequestItemReportType.NAME.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            if (!sortBy.equalsIgnoreCase("sequence")) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, SEQUENCE.description()));
            }
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        RequestItemReportSearchDto dto = requestItemReportService.searchRequestItemReportListByCondition(requestItemReportSearchRequest, pageable, organizationId);

        RequestItemReportSearchResponse response = RequestItemReportSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(dto.getTotal())
                .totalPage(dto.getTotalPage())
                .data(dto.getRequestItemReportDtoList())
                .build();

        if (dto.getRequestItemReportDtoList() != null && !dto.getRequestItemReportDtoList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/request-item-report/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequestItemReport(
            @RequestBody @Valid RequestItemReportRequest requestItemReportRequest,
            @RequestHeader(value = "organization", required = false) Integer organizationId
    ) {
        RequestItemReportDto dto = requestItemReportService.updateRequestItemReport(requestItemReportRequest, organizationId);
        if (dto != null) {
            return new ResponseEntity<>(new ApiResponse<>(dto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/request-item-report/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequestItemReportSequence(@RequestBody @Valid SequenceRequest request) {
        RequestItemReportDto dto = requestItemReportService.updateRequestItemReportSequence(request);
        if (dto != null) {
            return new ResponseEntity<>(new ApiResponse<>(dto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/request-item-report/{requestItemReportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteReportLine(@PathVariable("requestItemReportId") Integer requestItemReportId) {
        RequestItemReportDto dto = requestItemReportService.findRequestItemReportByRecId(requestItemReportId);
        if (dto != null) {
            if (!dto.isActive()) {
                boolean deleted = requestItemReportService.deleteRequestItemReport(requestItemReportId);
                if (deleted) {
                    return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(new ApiResponseStatus(
                        ApiMessage.E7100,
                        String.format(ApiMessage.E7100.description(),
                                "This request item report \"" + dto.getName() + "\" cannot be deleted because it is referenced by other entities.")
                ), HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }
}
