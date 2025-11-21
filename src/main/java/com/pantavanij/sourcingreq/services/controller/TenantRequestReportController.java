package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantRequestReportSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.TenantRequestReportSearchResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantRequestReportService;
import com.pantavanij.sourcingreq.services.util.Constant;
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

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class TenantRequestReportController {
    private final TenantRequestReportService tenantRequestReportService;

    @GetMapping(value = "/tenant-request-report/view/{requestReportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewTenantRequestReport(@PathVariable Integer requestReportId) {
        TenantRequestReportDto tenantRequestReportDto = tenantRequestReportService.getByRequestReportId(requestReportId);
        if (tenantRequestReportDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantRequestReportDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This requestReport id: " +requestReportId)), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SMQ')")
    @PostMapping(value = "/tenant-request-report/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchTenantRequestReport(@RequestBody @Valid TenantRequestReportSearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(Constant.SEQUENCE).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortBy != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC: Sort.Direction.ASC, sortBy));
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        TenantRequestReportSearchDto tenantRequestReportSearchDto = tenantRequestReportService.searchTenantRequestReportByCondition(request, pageable);
        TenantRequestReportSearchResponse response = TenantRequestReportSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(tenantRequestReportSearchDto.getTotal())
                .totalPage(tenantRequestReportSearchDto.getTotalPage())
                .data(tenantRequestReportSearchDto.getTenantRequestReportList())
                .build();

        if (tenantRequestReportSearchDto.getTenantRequestReportList() != null && !tenantRequestReportSearchDto.getTenantRequestReportList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMQ')")
    @PostMapping(value = "/tenant-request-report/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTenantRequestReport(@RequestBody @Valid RequestReportRequest request) {
        Integer result = tenantRequestReportService.createTenantRequestReport(request);
        if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This requestReport id: " + request.getId())), HttpStatus.NOT_FOUND);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7099, String.format(ApiMessage.E7099.description(), "This requestReport id: " + request.getId())), HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasAuthority('SMQ')")
    @PostMapping(value = "/tenant-request-report/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantRequestReport(@RequestBody @Valid RequestReportRequest request) {
        Integer result = tenantRequestReportService.updateTenantRequestReport(request);
        if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This requestReport id: " + request.getId())), HttpStatus.NOT_FOUND);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This requestReport id: " + request.getId())), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasAuthority('SMQ')")
    @PutMapping(value = "/tenant-request-report/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantRequestReportSequence(@RequestBody @Valid SequenceRequest request) {
        TenantRequestReportDto tenantRequestReportDto = tenantRequestReportService.updateTenantRequestReportSequence(request);
        if (tenantRequestReportDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantRequestReportDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
