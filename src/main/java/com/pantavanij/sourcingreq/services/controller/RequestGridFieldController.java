package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestGridFieldSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestGridFieldRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestGridFieldSequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.RequestGridFieldSearchResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.PathUrl;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestGridFieldService;
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
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class RequestGridFieldController {
    private final RequestGridFieldService requestGridFieldService;

    @GetMapping(value = "/request-grid-field")
    public ResponseEntity<ApiResponse<List<RequestGridFieldDto>>> getRequestGridField(@RequestParam PathUrl pathUrl, @RequestParam(value = "organizationId", required = false) Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        List<RequestGridFieldDto> requestGridFieldDtoList =
                requestGridFieldService.getRequestGridField(pathUrl.privilegeCode(), tenantCode, organizationId);

        if (requestGridFieldDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }

        return ResponseEntity.ok().body(new ApiResponse(requestGridFieldDtoList));
    }


    @GetMapping(value = "/request/grid-field/searchable")
    public ResponseEntity<ApiResponse<List<RequestGridFieldSearchableDto>>> getRequestGridFieldSearchable(@RequestParam PathUrl pathUrl,  @RequestParam(value = "organizationId", required = false) Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        List<RequestGridFieldSearchableDto> requestGridFieldDtoList =
                requestGridFieldService.getRequestGridFieldSearchable(pathUrl.privilegeCode(), tenantCode, organizationId);

        if (requestGridFieldDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }

        return ResponseEntity.ok().body(new ApiResponse(requestGridFieldDtoList));
    }

//    @PreAuthorize("hasAuthority('SAM')")
//    @PutMapping(value = "/request-grid-field/update", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity updateRequestGridField(@RequestBody @Valid RequestGridFieldRequest requestGridFieldRequest) {
//        RequestGridFieldDto requestGridFieldDto = requestGridFieldService.updateRequestGridField(requestGridFieldRequest);
//        if (requestGridFieldDto != null) {
//            return new ResponseEntity<>(new ApiResponse<>(requestGridFieldDto, new ApiResponseStatus()), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PreAuthorize("hasAuthority('SAM')")
//    @PutMapping(value = "/request-grid-field/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity updateRequestGridFieldSequence(@RequestBody @Valid SequenceRequest request) {
//        RequestGridFieldDto requestGridFieldDto = requestGridFieldService.updateRequestGridFieldSequence(request);
//        if (requestGridFieldDto != null) {
//            return new ResponseEntity<>(new ApiResponse<>(requestGridFieldDto), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @GetMapping(value = "/request-grid-field/cache")
//    public ResponseEntity<ApiResponse<RequestGridFieldSearchableDto>> getCache() {
//        String tenantCode = AppUtil.getTenantId();
//        List<RequestGridFieldSearchableDto> requestGridFieldDtoList = requestGridFieldService.getCache(tenantCode);
//        if (requestGridFieldDtoList.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ApiResponse("", new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
//        }
//
//        return ResponseEntity.ok().body(new ApiResponse(requestGridFieldDtoList));
//    }


//    @PostMapping(value = "/request-grid-field/cache")
//    public ResponseEntity<ApiResponse<RequestGridFieldSearchableDto>> refreshCache() {
//        String tenantCode = AppUtil.getTenantId();
//        List<RequestGridFieldSearchableDto> requestGridFieldDtoList = requestGridFieldService.refreshCache(tenantCode);
//        if (requestGridFieldDtoList.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ApiResponse("", new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
//        }
//        return ResponseEntity.ok().body(new ApiResponse("ok"));
//    }

    @PreAuthorize("hasAuthority('SAM')")
    @ControllerExecuteTime
    @PostMapping(value = "/request-grid-field/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequestGridField(@RequestBody @Valid RequestGridFieldSearchRequest requestGridFieldSearchRequest) {
        Sort sort;
        int page = requestGridFieldSearchRequest.getPage();
        int size = requestGridFieldSearchRequest.getPageSize();
        String sortBy = requestGridFieldSearchRequest.getSortBy();
        String sortOrder = requestGridFieldSearchRequest.getSortOrder();

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
        RequestGridFieldSearchDto requestGridFieldSearchDto = requestGridFieldService.searchRequestGridFieldByCondition(requestGridFieldSearchRequest, pageable);
        RequestGridFieldSearchResponse response = RequestGridFieldSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(requestGridFieldSearchDto.getPageSize())
                .page(page)
                .total(requestGridFieldSearchDto.getTotal())
                .totalPage(requestGridFieldSearchDto.getTotalPage())
                .data(requestGridFieldSearchDto.getRequestGridFields()).build();
        if (requestGridFieldSearchDto.getRequestGridFields() != null && !requestGridFieldSearchDto.getRequestGridFields().isEmpty()) {
            return ResponseEntity.ok().body(response);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/request-grid-field/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewRequestGridField(@RequestParam("recId") Integer recId, @RequestParam("privilegeCode") String privilegeCode) {
        RequestGridFieldDto requestGridFieldDto = requestGridFieldService.findRequestGridFieldByRecIdAndTenantIdAndPrivilegeCode(recId, privilegeCode);
        if (requestGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/request-grid-field/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createRequestGridField(@RequestBody @Valid RequestGridFieldRequest request) {
        RequestGridFieldDto requestGridFieldDto = requestGridFieldService.createRequestGridField(request);
        if (requestGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/request-grid-field/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequestGridField(@RequestBody @Valid RequestGridFieldRequest request) {
        RequestGridFieldDto requestGridFieldDto = requestGridFieldService.updateRequestGridField(request);
        if (requestGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/request-grid-field/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequestGridFieldSequence(@RequestBody @Valid RequestGridFieldSequenceRequest request) {
        RequestGridFieldDto requestGridFieldDto = requestGridFieldService.updateRequestGridFieldSequence(request);
        if (requestGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(requestGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
