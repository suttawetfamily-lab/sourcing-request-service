package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TypeService;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class TypeController {
    private final TypeService typeService;
    private final TenantService tenantService;

    @GetMapping(value = "/type")
    public ResponseEntity<ApiResponse<List<OptionDto>>> getType(@RequestParam(value = "report-filter", required = false) Boolean reportFilter) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        List<OptionDto> optionDtos = typeService.getTypeByTenantId(tenant.getRecId());
        if(reportFilter != null && reportFilter){
            OptionDto optionDto = new OptionDto("0","0","All-Type", true);
            optionDtos.add(0,optionDto);
        }
        return ResponseEntity.ok().body(new ApiResponse(optionDtos));
    }

    @GetMapping(value = "/type/view/{typeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewType(@PathVariable("typeId") Integer typeId) {
        TypeDto typeDto = typeService.getByTypeId(typeId);
        if (typeDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(typeDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SMT')")
    @PostMapping(value = "/type/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchType(@RequestBody @Valid TypeSearchRequest request) {
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
        TypeSearchDto typeSearchDto = typeService.searchTypeListByCondition(request, pageable);
        TypeSearchResponse response = TypeSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(typeSearchDto.getTotal())
                .totalPage(typeSearchDto.getTotalPage())
                .data(typeSearchDto.getTypeList())
                .build();

        if (typeSearchDto.getTypeList() != null && !typeSearchDto.getTypeList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SMT')")
    @PostMapping(value = "/type/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createType(@RequestBody @Valid TypeRequest request) {
        Integer result = typeService.createType(request);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SMT')")
    @PostMapping(value = "/type/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateType(@RequestBody @Valid TypeRequest request) {
        Integer result = typeService.updateType(request);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
        } else if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This type id: " + request.getId())), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SMT')")
    @PutMapping(value = "/type/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantCurrencySequence(@RequestBody @Valid SequenceRequest request) {
        TypeDto typeDto = typeService.updateTypeSequence(request);
        if (typeDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(typeDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
