package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.CurrencyService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
@Validated
public class CurrencyController {

    private final CurrencyService currencyService;
    private final TenantService tenantService;

    @GetMapping(value = "/currency", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getCurrencyBySearchTerm(@RequestParam String searchTerm) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());

        List<OptionDto> currencyOption = currencyService.getCurrencyByTenantIdAndSearchTerm(tenant.getRecId(), searchTerm);
        return ResponseEntity.ok().body(new ApiResponse(currencyOption));
    }

    @GetMapping(value = "/currency/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCurrencyByTenantId(@RequestParam Integer currencyId) {
        CurrencyDto currencyDto = currencyService.getCurrencyById(currencyId);
        if (currencyDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(currencyDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "Currency")), HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/currency/view/{currencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCurrencyByTenantIds(@PathVariable("currencyId") Integer currencyId) {
        CurrencyMasterDto currencyMasterDto = currencyService.getCurrencyMasterDataById(currencyId);
        if (currencyMasterDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(currencyMasterDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "Currency")), HttpStatus.OK);
    }

    @GetMapping(value = "currency/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllCurrency() {
        List<CurrencyDto> currencyDtoList = currencyService.getAllCurrency();
        if (currencyDtoList != null && !currencyDtoList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(currencyDtoList), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/currency/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchCurrency(@RequestBody @Valid CurrencySearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(Constant.CODE).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        CurrencySearchDto currencySearchDto = currencyService.searchCurrencyListByCondition(request, pageable);
        CurrencySearchResponse response = CurrencySearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(currencySearchDto.getTotal())
                .totalPage(currencySearchDto.getTotalPage())
                .data(currencySearchDto.getCurrencyList())
                .build();

        if (currencySearchDto.getCurrencyList() != null && !currencySearchDto.getCurrencyList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/currency/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCurrency(@RequestBody @Valid CurrencyMasterDataRequest request) {
        Integer result = currencyService.createCurrency(request);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7099, "Currency is duplicated with predefined currency. Please review and submit again."), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/currency/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCurrency(@RequestBody @Valid CurrencyMasterDataRequest request) {
        Integer result = currencyService.updateCurrency(request);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
        } else if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This currency id: " + request.getRecId())), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/currency/{currencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCurrency(@PathVariable("currencyId") Integer currencyId) {
        CurrencyDto currencyDto = currencyService.getCurrencyById(currencyId);
        if (currencyDto != null) {
            Integer result = currencyService.deleteCurrencyById(currencyId);
            if (result != 0 && result != -1) {
                return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
            } else if (result == 0) {
                return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This currency id: " + currencyId)), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new ApiResponseStatus(
                    ApiMessage.E7100,
                    String.format(ApiMessage.E7100.description(), "This currency \"" + currencyDto.getName() + "\" cannot be delete because the resource is referenced by other entities.")), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
    }

}
