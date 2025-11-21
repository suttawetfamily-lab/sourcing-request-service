package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.PathUrl;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.ProjectSearchType.NAME;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class TenantSectionController {
    private final TenantSectionService tenantSectionService;
    private final TenantSectionDetailDescriptionRepository tenantSectionDetailDescriptionRepository;
    private final DataSourceRepository dataSourceRepository;
    private final ValidatorRepository validatorRepository;

    @GetMapping(value = "/tenant-section/request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDto>>> getRequestFields(@RequestParam PathUrl pathUrl, @RequestParam(value = "organizationId", required = false) Integer organizationId) {
        List<TenantSectionDto> tenantSectionDtoList = tenantSectionService.getRequestFields(pathUrl.privilegeCode(), organizationId);
        if (tenantSectionDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse(tenantSectionDtoList));
    }

    @GetMapping(value = "/tenant-section/request-item/type/{requestTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDto>>> getRequestItemFields(
            @PathVariable Integer requestTypeId,
            @RequestParam("organizationId") Integer organizationId) {

        List<TenantSectionDto> tenantSectionDtoList =
                tenantSectionService.getRequestItemFields(requestTypeId, organizationId);

        if (tenantSectionDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDtoList));
    }

    @GetMapping(value = "/tenant-section/request-item-header/type/{requestTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDto>>> getRequestItemHeaderFields(
            @PathVariable Integer requestTypeId,
            @RequestParam("organizationId") Integer organizationId) {

        List<TenantSectionDto> tenantSectionDtoList =
                tenantSectionService.getRequestItemHeaderFields(requestTypeId, organizationId);

        if (tenantSectionDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDtoList));
    }


    @GetMapping(value = "/tenant-section/existing-price-item/type/{requestTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDto>>> getExistingPriceItemFields(
            @PathVariable Integer requestTypeId,
            @RequestParam(value = "organizationId", required = false) Integer organizationId) {

        List<TenantSectionDto> tenantSectionDtoList = tenantSectionService.getExistingPriceItemFields(requestTypeId, organizationId);

        if (tenantSectionDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDtoList));
    }


    @GetMapping(value = "/tenant-section/existing-price-item-header/type/{requestTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDto>>> getExistingPriceItemHeaderFields(
            @PathVariable Integer requestTypeId,
            @RequestParam(value = "organizationId", required = false) Integer organizationId) {

        List<TenantSectionDto> tenantSectionDtoList = tenantSectionService.getExistingPriceItemHeaderFields(requestTypeId, organizationId);

        if (tenantSectionDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDtoList));
    }


    @GetMapping(value = "/tenant-section/exceptional-sourcing-item/type/{requestTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDto>>> getExceptionalSourcingItemFields(
            @PathVariable Integer requestTypeId,
            @RequestParam(value = "organizationId", required = false) Integer organizationId) {

        List<TenantSectionDto> tenantSectionDtoList = tenantSectionService.getExceptionalSourcingItemFields(requestTypeId, organizationId);

        if (tenantSectionDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDtoList));
    }


    @GetMapping(value = "/tenant-section/exceptional-sourcing-item-header/type/{requestTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDto>>> getExceptionalSourcingItemHeaderFields(
            @PathVariable Integer requestTypeId,
            @RequestParam(value = "organizationId", required = false) Integer organizationId) {

        List<TenantSectionDto> tenantSectionDtoList = tenantSectionService.getExceptionalSourcingItemHeaderFields(requestTypeId, organizationId);

        if (tenantSectionDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDtoList));
    }


    @GetMapping(value = "/tenant-section/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDto>>> getRequestReportFields(
            @RequestParam(value = "organizationId", required = false) Integer organizationId) {

        List<TenantSectionDto> tenantSectionDtoList = tenantSectionService.getRequestReportFields(organizationId);

        if (tenantSectionDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDtoList));
    }


    @GetMapping(value = "/tenant-section/request-preview/{pathUrl}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDetailDto>>> getRequestPreviewFields(
            @RequestParam(value = "mode") String mode,
            @Valid @PathVariable String pathUrl,
            @RequestParam(value = "organizationId") Integer organizationId) {

        List<TenantSectionDetailDto> tenantSectionDetailDtoList = tenantSectionService.getRequestPreviewFields(mode, pathUrl, organizationId);

        if (tenantSectionDetailDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDetailDtoList));
    }


    @GetMapping(value = "/tenant-section/request-full-preview", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TenantSectionDto>>> getRequestFullPreviewFields(
            @RequestParam("organizationId") Integer organizationId) {

        List<TenantSectionDto> tenantSectionDtoList =
                tenantSectionService.getRequestFullPreviewFields(organizationId);

        if (tenantSectionDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }

        return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDtoList));
    }


    @GetMapping(value = "/tenant-section/options")
    public ResponseEntity<ApiResponse<List<OptionDto>>> getOptionList(@RequestParam(value = "option-name", required = true) String optionName) {
        List<OptionDto> optionDtos = tenantSectionService.getOptionList(optionName);
        return ResponseEntity.ok().body(new ApiResponse(optionDtos));
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/tenant-section/view/{tenantSectionId}")
    public ResponseEntity getTenantSectionView(@PathVariable(value = "tenantSectionId") Integer tenantSectionId) {
        TenantSectionDto tenantSectionDto = tenantSectionService.getByRecId(tenantSectionId);
        if (tenantSectionDto != null) {
            return ResponseEntity.ok().body(new ApiResponse<>(tenantSectionDto));
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/tenant-section/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity tenantSectionSearch(@RequestBody TenantSectionSearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(NAME.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortBy != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC: Sort.Direction.ASC, sortBy));
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        TenantSectionSearchDto tenantSectionSearchDto = tenantSectionService.searchTenantSectionByCondition(request, pageable);
        TenantSectionSearchResponse response = TenantSectionSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(tenantSectionSearchDto.getPageSize())
                .page(page)
                .totalPage(tenantSectionSearchDto.getTotalPage())
                .total(tenantSectionSearchDto.getTotal())
                .data(tenantSectionSearchDto.getTenantSectionList())
                .build();

        if (tenantSectionSearchDto.getTenantSectionList() != null && !tenantSectionSearchDto.getTenantSectionList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/tenant-section/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNewTenantSection(@RequestBody TenantSectionRequest request) {
        TenantSectionDto tenantSectionDto = tenantSectionService.createNewTenantSection(request, request.getOrganizationId());
        if (tenantSectionDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantSectionDto), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/tenant-section/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTenantSection(@RequestBody TenantSectionRequest request) {
        TenantSectionDto tenantSectionDto = tenantSectionService.updateTenantSection(request, request.getOrganizationId());
        if (tenantSectionDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantSectionDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/tenant-section/delete/{tenantSectionId}")
    public ResponseEntity<?> deleteTenantSection(@PathVariable(value = "tenantSectionId") Integer tenantSectionId) {
        Boolean isDeleted = tenantSectionService.deleteTenantSection(tenantSectionId, null);
        if (isDeleted) {
            return new ResponseEntity<>(new ApiResponse<>(null), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
