package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantSourcingStatusSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantSourcingStatusRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSourcingStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
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

import static com.pantavanij.sourcingreq.services.enums.ProjectSearchType.NAME;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class TenantSourcingStatusController {
    private final TenantSourcingStatusService tenantSourcingStatusService;
    private final UaaService uaaService;

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/tenant-sourcing-status/view/{sourcingStatusId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTenantSourcingStatus(@PathVariable("sourcingStatusId") Integer sourcingStatusId) {
        TenantSourcingStatusDto tenantSourcingStatusDto = tenantSourcingStatusService.getBySourcingStatusIdAndTenant(sourcingStatusId);
        if (tenantSourcingStatusDto != null) {
            return ResponseEntity.ok().body(new ApiResponse<>(tenantSourcingStatusDto));
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "Data")), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SMN')")
    @PostMapping(value = "/tenant-sourcing-status/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchTenantSourcingStatus(@RequestBody @Valid TenantSourcingStatusSearchRequest request) {
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
        TenantSourcingStatusSearchDto tenantSourcingStatusSearchDto = tenantSourcingStatusService.searchTenantSourcingStatusByCondition(request, pageable);
        TenantSourcingStatusSearchResponse response = TenantSourcingStatusSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(tenantSourcingStatusSearchDto.getTotal())
                .totalPage(tenantSourcingStatusSearchDto.getTotalPage())
                .data(tenantSourcingStatusSearchDto.getTenantSourcingStatusList())
                .build();

        if (tenantSourcingStatusSearchDto.getTenantSourcingStatusList() != null && !tenantSourcingStatusSearchDto.getTenantSourcingStatusList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }


//    @ControllerExecuteTime
//    @PreAuthorize("hasAuthority('SAM')")
//    @PostMapping(value = "/tenant-sourcing-status/submit", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity submitTenantSourcingStatus(@RequestBody TenantSourcingStatusRequest request) {
//        TenantSourcingStatusDto tenantSourcingStatusDto = tenantSourcingStatusService.createTenantSourcingStatus(request);
//        if (tenantSourcingStatusDto != null) {
//            return new ResponseEntity<>(new ApiResponse<>(tenantSourcingStatusDto, new ApiResponseStatus(ApiMessage.I1002, ApiMessage.I1002.description())), HttpStatus.CREATED);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @ControllerExecuteTime
    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/tenant-sourcing-status/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantSourcingStatus(@RequestBody TenantSourcingStatusRequest request) {
        TenantSourcingStatusDto tenantSourcingStatusDto = tenantSourcingStatusService.updateTenantSourcingStatus(request);
        if (tenantSourcingStatusDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantSourcingStatusDto, new ApiResponseStatus(ApiMessage.I1002, ApiMessage.I1002.description())), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMN')")
    @PutMapping(value = "/tenant-sourcing-status/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantSourcingStatusSequence(@RequestBody @Valid SequenceRequest request) {
        TenantSourcingStatusDto tenantSourcingStatusDto = tenantSourcingStatusService.updateTenantSourcingStatusSequence(request);
        if (tenantSourcingStatusDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantSourcingStatusDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
