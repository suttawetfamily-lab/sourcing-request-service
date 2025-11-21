package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final UaaService uaaService;

    @GetMapping(value = "/tenant", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTenant(){
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        TenantDto tenantDto =  TenantMapper.INSTANCE.toTenantDto(tenant);

        if(tenant != null){
            return new ResponseEntity<>(new TenantResponse(tenantDto), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7016, ApiMessage.E7016.description()) , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/tenant/view/{tenantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewTenant(@PathVariable("tenantId") Integer tenantId) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        TenantDto tenantDto = tenantService.getTenantDtoByRecId(tenantId, timeZone);
        if (tenantDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/tenant/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchTenant(@RequestBody @Valid TenantSearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(Constant.CODE).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC: Sort.Direction.ASC, sortBy));
            sort = Sort.by(orders);
        }

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        TenantSearchDto tenantSearchDto = tenantService.searchTenantListByCondition(request, pageable, timeZone);
        TenantSearchResponse response = TenantSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(tenantSearchDto.getTotal())
                .totalPage(tenantSearchDto.getTotalPage())
                .data(tenantSearchDto.getTenantList())
                .build();

        if (tenantSearchDto.getTenantList() != null && !tenantSearchDto.getTenantList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/tenant/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTenant(@RequestBody @Valid TenantRequest request) {
        Integer result = tenantService.createTenant(request);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(
                    ApiMessage.E7099,
                    String.format(ApiMessage.E7099.description(), "This tenant: " + request.getCode())
            ), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/tenant/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenant(@RequestBody @Valid TenantRequest request) {
        Integer result = tenantService.updateTenant(request);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
        } else if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This tenant id: " + request.getRecId())), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
