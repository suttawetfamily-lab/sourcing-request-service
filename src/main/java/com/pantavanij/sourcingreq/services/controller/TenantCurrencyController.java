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
public class TenantCurrencyController {
    private final TenantCurrencyService tenantCurrencyService;
    private final TenantService tenantService;

    // TenantCurrencyController.java
    @GetMapping(value = "/tenant-currency", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getTenantCurrencyBySearchTerm(
            @RequestParam String searchTerm,
            @RequestHeader(value = "organization", required = true) Integer organizationId) {

        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        if (tenant == null) {
            throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());
        }

        List<OptionDto> tenantCurrencyOption =
                tenantCurrencyService.getTenantCurrencyByTenantIdAndOrganizationIdAndSearchTerm(
                        tenant.getRecId(), organizationId, searchTerm);

        return ResponseEntity.ok().body(new ApiResponse<>(tenantCurrencyOption));
    }



    @GetMapping(value = "/tenant-currency/view/{currencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewTenantCurrency(@PathVariable Integer currencyId) {
        TenantCurrencyDto tenantCurrencyDto = tenantCurrencyService.getByCurrencyId(currencyId);
        if (tenantCurrencyDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantCurrencyDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This currency id: " +currencyId)), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SMN')")
    @PostMapping(value = "/tenant-currency/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchTenantCurrency(@RequestBody @Valid TenantCurrencySearchRequest request) {
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
        TenantCurrencySearchDto tenantCurrencySearchDto = tenantCurrencyService.searchTenantCurrencyByCondition(request, pageable);
        TenantCurrencySearchResponse response = TenantCurrencySearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(tenantCurrencySearchDto.getTotal())
                .totalPage(tenantCurrencySearchDto.getTotalPage())
                .data(tenantCurrencySearchDto.getTenantCurrencyList())
                .build();

        if (tenantCurrencySearchDto.getTenantCurrencyList() != null && !tenantCurrencySearchDto.getTenantCurrencyList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMN')")
    @PostMapping(value = "/tenant-currency/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTenantCurrency(@RequestBody @Valid CurrencyRequest request) {
        Integer result = tenantCurrencyService.createTenantCurrency(request);
        if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This currency id: " + request.getId())), HttpStatus.NOT_FOUND);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7099, String.format(ApiMessage.E7099.description(), "Currency is duplicated with predefined currency. Please review and submit again.")), HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasAuthority('SMN')")
    @PostMapping(value = "/tenant-currency/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantCurrency(@RequestBody @Valid CurrencyRequest request) {
        Integer result = tenantCurrencyService.updateTenantCurrency(request);
        if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This currency id: " + request.getId())), HttpStatus.NOT_FOUND);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This tenant-currency id: " + request.getId())), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasAuthority('SMN')")
    @PutMapping(value = "/tenant-currency/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantCurrencySequence(@RequestBody @Valid SequenceRequest request) {
        TenantCurrencyDto tenantCurrencyDto = tenantCurrencyService.updateTenantCurrencySequence(request);
        if (tenantCurrencyDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantCurrencyDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/tenant-currency/{currencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCurrency(@PathVariable("currencyId") Integer currencyId) {
        Integer result = tenantCurrencyService.deleteTenantCurrencyById(currencyId);
        if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This currency id: " + currencyId)), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
    }
}
