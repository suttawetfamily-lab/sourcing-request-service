package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestReportSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.RequestReportSearchResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.SearchRequestReportType;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestReportService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestReportService;
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

import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.version}")
public class RequestReportController {

    private final RequestReportService requestReportService;

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/request-report/view/{requestReportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewRequestReport(@PathVariable("requestReportId") Integer requestReportId) {
        RequestReportDto requestReportDto = requestReportService.findRequestReportByRecId(requestReportId);
        if (requestReportDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestReportDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/request-report/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createRequestReport(@RequestBody @Valid RequestReportRequest requestReportRequest) {
        RequestReportDto requestReportDto = requestReportService.createRequestReport(requestReportRequest);
        if (requestReportDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestReportDto, new ApiResponseStatus()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/request-report/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequestReport(@RequestBody @Valid RequestReportSearchRequest requestReportSearchRequest) {
        Sort sort;
        int page = requestReportSearchRequest.getPage();
        int size = requestReportSearchRequest.getPageSize();
        String sortBy = requestReportSearchRequest.getSortBy();
        String sortOrder = requestReportSearchRequest.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(SearchRequestReportType.NAME.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortBy != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC: Sort.Direction.ASC, sortBy));
            if (!sortBy.equalsIgnoreCase("sequence")) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, SEQUENCE.description()));
            }
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        RequestReportSearchDto requestReportSearchDto = requestReportService.searchRequestReportListByCondition(requestReportSearchRequest, pageable);
        RequestReportSearchResponse response = RequestReportSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(requestReportSearchDto.getTotal())
                .totalPage(requestReportSearchDto.getTotalPage())
                .data(requestReportSearchDto.getRequestReportDtoList())
                .build();

        if (requestReportSearchDto.getRequestReportDtoList() != null && !requestReportSearchDto.getRequestReportDtoList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/request-report/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequestReport(@RequestBody @Valid RequestReportRequest requestReportRequest) {
        RequestReportDto requestReportDto = requestReportService.updateRequestReport(requestReportRequest);
        if (requestReportDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestReportDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/request-report/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequestReportSequence(@RequestBody @Valid SequenceRequest request) {
        RequestReportDto requestReportDto = requestReportService.updateRequestReportSequence(request);
        if (requestReportDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestReportDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/request-report/{requestReportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteReportLine(@PathVariable("requestReportId") Integer requestReportId) {
        RequestReportDto requestReportDto = requestReportService.findRequestReportByRecId(requestReportId);
        if (requestReportDto != null) {
            if(!requestReportDto.isActive()){
                boolean isDeleted = requestReportService.deleteRequestReport(requestReportId);
                if (isDeleted) {
                    return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(new ApiResponseStatus(
                        ApiMessage.E7100,
                        String.format(ApiMessage.E7100.description(), "This request report \"" + requestReportDto.getName() + "\" cannot be delete because the resource is referenced by other entities.")), HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

}
