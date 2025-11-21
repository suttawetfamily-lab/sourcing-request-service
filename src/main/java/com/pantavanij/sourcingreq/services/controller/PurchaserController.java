package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.interceptor.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.PurchaserService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.validation.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.SearchPurchaserType.PURCHASER_NAME;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
@Validated
public class PurchaserController {

    private final PurchaserService purchaserService;
    private final TenantService tenantService;

    @GetMapping(value = "/purchaser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDetailDto>>> getPurchaserBySearchTerm(
            @RequestParam @SearchTermConstraint String searchTerm,
            @RequestParam @Nullable Integer categoryId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());

        List<OptionDetailDto> purchaserOption = purchaserService.getPurchaserByTenantIdAndSearchTermAndCategoryId(
                tenant.getRecId(),
                searchTerm,
                categoryId);
        return ResponseEntity.ok().body(new ApiResponse(purchaserOption));
    }

    @PreAuthorize("hasAuthority('SMP')")
    @GetMapping(value = "/purchaser/view/{purchaserId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewPurchaser(@PathVariable ("purchaserId") Integer purchaserId) {
        PurchaserDto purchaserDto = purchaserService.findPurchaserByRecId(purchaserId);
        if (purchaserDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(purchaserDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }

    }

    @PreAuthorize("hasAuthority('SMP')")
    @PostMapping(value = "/purchasers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllPurchaser(@RequestBody @Valid EPAuthUserSearchRequest epaAuthUserSearchRequest) {
        List<EPAuthUserDTO> epAuthUsers = purchaserService.getAllPurchaser(epaAuthUserSearchRequest);
        if (epAuthUsers != null) {
            return new ResponseEntity<>(new ApiResponse<>(epAuthUsers), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @ControllerExecuteTime
    @PreAuthorize("hasAuthority('SMP')")
    @PostMapping(value = "/purchaser/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createPurchaser(@RequestBody @Valid PurchaserRequest purchaserRequest) {
        PurchaserDto purchaserDto = purchaserService.createPurchaser(purchaserRequest);
        if (purchaserDto != null && purchaserDto.getRecId() != null) {
            return new ResponseEntity<>(new ApiResponse<>(purchaserDto, new ApiResponseStatus(ApiMessage.I1002, ApiMessage.I1002.description())), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMP')")
    @PostMapping(value = "/purchaser/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchPurchaser(@RequestBody @Valid PurchaserSearchRequest purchaserSearchRequest) {
        Sort sort;
        int page = purchaserSearchRequest.getPage();
        int size = purchaserSearchRequest.getPageSize();
        String sortBy = purchaserSearchRequest.getSortBy();
        String sortOrder = purchaserSearchRequest.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(PURCHASER_NAME.description()).descending();
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
        PurchaserSearchDto purchaserSearchDto = purchaserService.searchPurchaserByCondition(purchaserSearchRequest, pageable);
        PurchaserSearchResponse response = PurchaserSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(purchaserSearchDto.getPageSize())
                .page(page)
                .totalPage(purchaserSearchDto.getTotalPage())
                .total(purchaserSearchDto.getTotal())
                .data(purchaserSearchDto.getPurchaserList())
                .build();

        if (purchaserSearchDto.getPurchaserList() != null && !purchaserSearchDto.getPurchaserList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMP')")
    @PutMapping(value = "/purchaser/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updatePurchaser(@RequestBody @Valid PurchaserRequest purchaserRequest) {
        PurchaserDto purchaserDto = purchaserService.updatePurchaser(purchaserRequest);
        if (purchaserDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(purchaserDto, new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description())), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMP')")
    @PutMapping(value = "/purchaser/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updatePurchaserSequence(@RequestBody @Valid SequenceRequest request) {
        PurchaserDto purchaserDto = purchaserService.updatePurchaserSequence(request);
        if (purchaserDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(purchaserDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/purchaser/{purchaserId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deletePurchaser(@PathVariable("purchaserId") Integer purchaserId) {
        boolean isDeleted = purchaserService.deletePurchaserByRecId(purchaserId);
        if (isDeleted) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
