package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestItemReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantRequestItemReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantRequestItemReportSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.TenantRequestItemReportSearchResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantRequestItemReportService;
import com.pantavanij.sourcingreq.services.util.Constant;
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

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class TenantRequestItemReportController {

    private final TenantRequestItemReportService tenantRequestItemReportService;

    @GetMapping(value = "/tenant-request-item-report/view/{requestItemReportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewTenantRequestItemReport(
            @PathVariable Integer requestItemReportId,
            @RequestHeader(value = "organization", required = false) Integer organizationId
    ) {
        List<TenantRequestItemReportDto> tenantRequestItemReportDtoList =
                tenantRequestItemReportService.getByRequestItemReportId(requestItemReportId, organizationId);

        if (tenantRequestItemReportDtoList == null || tenantRequestItemReportDtoList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponseStatus(
                    ApiMessage.E7096,
                    String.format(ApiMessage.E7096.description(), "This requestItemReport id: " + requestItemReportId)
            ), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse<>(tenantRequestItemReportDtoList), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SRP')")
    @PostMapping(value = "/tenant-request-item-report/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchTenantRequestItemReport(
            @RequestBody @Valid TenantRequestItemReportSearchRequest request,
            @RequestHeader(value = "organization", required = false) Integer organizationId
    ) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (StringUtils.isEmpty(sortBy) && StringUtils.isEmpty(sortOrder)) {
            sort = Sort.by(Constant.SEQUENCE).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        TenantRequestItemReportSearchDto tenantRequestItemReportSearchDto =
                tenantRequestItemReportService.searchTenantRequestItemReportByCondition(request, pageable, organizationId);

        TenantRequestItemReportSearchResponse response = TenantRequestItemReportSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(tenantRequestItemReportSearchDto.getTotal())
                .totalPage(tenantRequestItemReportSearchDto.getTotalPage())
                .data(tenantRequestItemReportSearchDto.getTenantRequestItemReportList())
                .build();

        if (tenantRequestItemReportSearchDto.getTenantRequestItemReportList() != null &&
                !tenantRequestItemReportSearchDto.getTenantRequestItemReportList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SRP')")
    @PostMapping(value = "/tenant-request-item-report/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTenantRequestItemReport(
            @RequestBody @Valid TenantRequestItemReportRequest request,
            @RequestHeader(value = "organization", required = false) Integer organizationId
    ) {
        TenantRequestItemReportDto result = tenantRequestItemReportService.createTenantRequestItemReport(request, organizationId);
        if (result != null) {
            return new ResponseEntity<>(new ApiResponse<>(result, new ApiResponseStatus()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SRP')")
    @PostMapping(value = "/tenant-request-item-report/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantRequestItemReport(
            @RequestBody @Valid TenantRequestItemReportRequest request,
            @RequestHeader(value = "organization", required = false) Integer organizationId
    ) {
        Integer result = tenantRequestItemReportService.updateTenantRequestItemReport(request, organizationId);
        if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096,
                    String.format(ApiMessage.E7096.description(), "This requestItemReport id: " + request.getId())),
                    HttpStatus.NOT_FOUND);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096,
                    String.format(ApiMessage.E7096.description(), "This requestItemReport id: " + request.getId())),
                    HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasAuthority('SRP')")
    @PutMapping(value = "/tenant-request-item-report/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantRequestItemReportSequence(
            @RequestBody @Valid SequenceRequest request,
            @RequestHeader(value = "organization", required = false) Integer organizationId
    ) {
        TenantRequestItemReportDto dto = tenantRequestItemReportService.updateTenantRequestItemReportSequence(request, organizationId);
        if (dto != null) {
            return new ResponseEntity<>(new ApiResponse<>(dto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
