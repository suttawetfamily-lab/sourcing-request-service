package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemGridFieldSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemGridFieldRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemGridFieldSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemGridFieldSequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.PathUrl;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemGridFieldService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
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

import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class RequestItemGridFieldController {
    private final RequestItemGridFieldService requestItemGridFieldService;

    @GetMapping(value = "/request-item-grid-field/type/{typeId}")
    public ResponseEntity<ApiResponse<List<RequestItemGridFieldDto>>> getRequestItemGridField(
            @PathVariable Integer typeId,
            @RequestParam PathUrl pathUrl,
            @RequestParam(value = "organizationId", required = false) Integer organizationId) {

        String tenantCode = AppUtil.getTenantId();
        List<RequestItemGridFieldDto> dtoList =
                requestItemGridFieldService.getRequestItemGridField(pathUrl.privilegeCode(), tenantCode, typeId, organizationId);

        if (dtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(dtoList));
    }

    @ControllerExecuteTime
    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/request-item-grid-field/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequestItemGridField(@RequestBody @Valid RequestItemGridFieldSearchRequest requestItemGridFieldSearchRequest) {
        Sort sort;
        int page = requestItemGridFieldSearchRequest.getPage();
        int size = requestItemGridFieldSearchRequest.getPageSize();
        String sortBy = requestItemGridFieldSearchRequest.getSortBy();
        String sortOrder = requestItemGridFieldSearchRequest.getSortOrder();

        if ("".equals(sortOrder) || sortOrder == null && "".equals(sortBy) || sortBy == null) {
            sort = Sort.by("sequence").descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortOrder != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            if (!sortBy.equalsIgnoreCase("sequence")) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, SEQUENCE.description()));
            }
            sort = Sort.by(orders);
        }
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        RequestItemGridFieldSearchDto requestItemGridFieldSearchDto = requestItemGridFieldService.searchRequestItemGridFieldByCondition(requestItemGridFieldSearchRequest, pageable);
        RequestItemGridFieldSearchResponse response = RequestItemGridFieldSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(requestItemGridFieldSearchDto.getPageSize())
                .page(page)
                .total(requestItemGridFieldSearchDto.getTotal())
                .totalPage(requestItemGridFieldSearchDto.getTotalPage())
                .data(requestItemGridFieldSearchDto.getRequestItemGridFields()).build();
        if (requestItemGridFieldSearchDto.getRequestItemGridFields() != null && !requestItemGridFieldSearchDto.getRequestItemGridFields().isEmpty()) {
            return ResponseEntity.ok().body(response);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/request-item-grid-field/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewRequestItemGridField(@RequestParam("recId") Integer recId, @RequestParam("privilegeCode") String privilegeCode,
                                                   @RequestParam(value = "organizationId", required = false) Integer organizationId) {
        RequestItemGridFieldDto requestItemGridFieldDto = requestItemGridFieldService.findRequestItemGridFieldByRecIdAndTenantIdAndPrivilegeCode(recId, privilegeCode, organizationId);
        if (requestItemGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestItemGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/request-item-grid-field/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createRequestItemGridField(@RequestBody @Valid RequestItemGridFieldRequest request) {
        RequestItemGridFieldDto requestItemGridFieldDto = requestItemGridFieldService.createRequestItemGridField(request);
        if (requestItemGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestItemGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/request-item-grid-field/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequestItemGridField(@RequestBody @Valid RequestItemGridFieldRequest request) {
        RequestItemGridFieldDto requestItemGridFieldDto = requestItemGridFieldService.updateRequestItemGridField(request);
        if (requestItemGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestItemGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/request-item-grid-field/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequestItemGridFieldSequence(@RequestBody @Valid RequestItemGridFieldSequenceRequest request) {
        RequestItemGridFieldDto requestItemGridFieldDto = requestItemGridFieldService.updateRequestItemGridFieldSequence(request);
        if (requestItemGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestItemGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
