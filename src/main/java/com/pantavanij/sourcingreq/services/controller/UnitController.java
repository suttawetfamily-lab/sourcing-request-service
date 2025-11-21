package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UnitService;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
@Validated
public class UnitController {

    private final UnitService unitService;
    private final TenantService tenantService;

    @GetMapping(value = "/unit/{tenantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnitByTenantIdV1(@PathVariable Integer tenantId, @RequestHeader(value = "organization", required = false) Integer organizationId){
        List<UnitDto> unitList = unitService.getUnitByTenantIdV1(tenantId, organizationId);

        if (!unitList.isEmpty()) {
            return new ResponseEntity<>(new UnitListResponse(unitList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7008, ApiMessage.E7008.description()) , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/unit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getUnitBySearchTerm(@RequestParam @SearchTermConstraint String searchTerm, @RequestHeader(value = "organization", required = false) Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());

        List<OptionDto> unitOption = unitService.getUnitSearchTerm(tenant.getRecId(), searchTerm, organizationId);
        return ResponseEntity.ok().body(new ApiResponse(unitOption));
    }

    @GetMapping(value = "/unit/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUnit() {
        List<OptionDto> unitDtoList = unitService.getAllUnit();
        if (!unitDtoList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(unitDtoList), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7096.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/unit/view/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnitByUnitId(@PathVariable Integer unitId) {
        UnitMasterDataDto unitDto = unitService.getUnitByUnitId(unitId);
        if (unitDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(unitDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7008, ApiMessage.E7008.description()) , HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/unit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnitBySearchTermV1(@RequestParam @SearchTermConstraint String searchTerm, @RequestHeader(value = "organization", required = false) Integer organizationId) {

        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<UnitDto> unitList = unitService.getUnitSearchTermV1(tenant.getRecId(), searchTerm, organizationId);

        if (!unitList.isEmpty()) {
            return new ResponseEntity<>(new UnitListResponse(unitList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7008, ApiMessage.E7008.description()) , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/unit/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchUnit(@RequestBody @Valid UnitSearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(Constant.CODE).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortBy != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC: Sort.Direction.ASC, sortBy));
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        UnitSearchDto unitSearchDto = unitService.searchUnitsByCondition(request, pageable);
        UnitSearchResponse response = UnitSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(unitSearchDto.getTotal())
                .totalPage(unitSearchDto.getTotalPage())
                .data(unitSearchDto.getUnitList())
                .build();

        if (unitSearchDto.getUnitList() != null && !unitSearchDto.getUnitList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/unit/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUnit(@RequestBody UnitMasterDataRequest request) {
        Integer result = unitService.createUnit(request);
        if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7099, "Unit is duplicated with predefined unit. Please review and submit again."), HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/unit/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateUnit(@RequestBody @Valid UnitMasterDataRequest request) {
        Integer result = unitService.updateUnit(request);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
        } else if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This unit id: " + request.getRecId())), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/unit/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUnit(@PathVariable("unitId") Integer unitId) {
        UnitMasterDataDto unitDto = unitService.getUnitByUnitId(unitId);
        if (unitDto != null) {
            Integer result = unitService.deleteUnitById(unitId);
            if (result != 0 && result != -1) {
                return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
            } else if (result == 0) {
                return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This unit id: " + unitId)), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new ApiResponseStatus(
                    ApiMessage.E7100,
                    String.format(ApiMessage.E7100.description(), "This unit \"" + unitDto.getName() + "\" cannot be delete because the resource is referenced by other entities.")), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
    }

}


