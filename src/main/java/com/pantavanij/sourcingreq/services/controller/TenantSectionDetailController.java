package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class TenantSectionDetailController {

    private final TenantSectionDetailService tenantSectionDetailService;

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/tenant-section-detail/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTenantSectionDetail(@RequestBody TenantSectionDetailRequest request) {
        TenantSectionDetailDto tenantSectionDetailDto = tenantSectionDetailService.createTenantSectionDetail(request);
        if (tenantSectionDetailDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantSectionDetailDto), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/tenant-section-detail/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantSectionDetail(@RequestBody TenantSectionDetailRequest request) {
        TenantSectionDetailDto tenantSectionDetailDto = tenantSectionDetailService.updateTenantSectionDetail(request);
        if (tenantSectionDetailDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantSectionDetailDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = "/tenant-section-detail/field-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTenantSectionDetailFieldName(
            @RequestParam(value = "tenantSectionId", required = false) Integer tenantSectionId,
            @RequestParam(value = "tenantSectionTypes", required = false) String tenantSectionTypes,
            @RequestHeader(value = "organization", required = false) Integer organizationId) {
        List<TenantSectionDetailFieldNameDto> fieldNameDtoList = null;

        if (tenantSectionId != null) {
            fieldNameDtoList = tenantSectionDetailService.getTenantSectionDetailFieldName(tenantSectionId, organizationId);
        } else if (tenantSectionTypes != null) {
            fieldNameDtoList = tenantSectionDetailService.getTenantSectionDetailFieldNameByTypes(tenantSectionTypes, organizationId);
        }

        if (fieldNameDtoList != null && !fieldNameDtoList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(fieldNameDtoList), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
