package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class TenantUnitController {
    private final TenantUnitService tenantUnitService;
    private final TenantService tenantService;

    @GetMapping(value = "/tenant-unit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getTenantUnitBySearchTerm(
            @RequestParam String searchTerm,
            @RequestHeader(value = "organization", required = true) Integer organizationId) {

        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)
            throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());

        List<OptionDto> tenantUnitOption =
                tenantUnitService.getTenantUnitByTenantIdAndSearchTerm(tenant.getRecId(), organizationId, searchTerm);

        return ResponseEntity.ok().body(new ApiResponse<>(tenantUnitOption));
    }


    @PreAuthorize("hasAuthority('SMU')")
    @GetMapping(value = "/tenant-unit/view/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTenantUnit(@PathVariable("unitId") Integer unitId) {
        TenantUnitDto tenantUnitDto = tenantUnitService.getByUnitId(unitId);
        if (tenantUnitDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantUnitDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This tenant-unit id: " + unitId)), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SMU')")
    @PostMapping(value = "tenant-unit/search")
    public ResponseEntity searchUnit(@RequestBody @Valid UnitSearchRequest request) {
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
        TenantUnitSearchDto tenantUnitSearchDto = tenantUnitService.searchTenantUnitByCondition(request, pageable);
        TenantUnitSearchResponse response = TenantUnitSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(tenantUnitSearchDto.getTotal())
                .totalPage(tenantUnitSearchDto.getTotalPage())
                .data(tenantUnitSearchDto.getTenantUnitList())
                .build();

        if (tenantUnitSearchDto.getTenantUnitList() != null && !tenantUnitSearchDto.getTenantUnitList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMU')")
    @PostMapping(value = "/tenant-unit/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTenantUnit(@RequestBody @Valid UnitRequest request) {
        Integer result = tenantUnitService.createTenantUnit(request);
        if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This unit id: " + request.getId())), HttpStatus.NOT_FOUND);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7099, String.format(ApiMessage.E7099.description(), "Unit is duplicated with predefined unit. Please review and submit again.")), HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasAuthority('SMU')")
    @PostMapping(value = "/tenant-unit/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantUnit(@RequestBody @Valid UnitRequest request) {
        Integer result = tenantUnitService.updateTenantUnit(request);
        if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This unit id: " + request.getId())), HttpStatus.NOT_FOUND);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This tenant-unit id: " + request.getId())), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasAuthority('SMU')")
    @PutMapping(value = "/tenant-unit/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantUnitSequence(@RequestBody @Valid SequenceRequest request) {
        TenantUnitDto tenantUnitDto = tenantUnitService.updateTenantUnitSequence(request);
        if (tenantUnitDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantUnitDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/tenant-unit/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteTenantUnit(@PathVariable("unitId") Integer unitId) {
        Integer result = tenantUnitService.deleteTenantUnitById(unitId);
        if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This unit id: " + unitId)), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
    }

}
