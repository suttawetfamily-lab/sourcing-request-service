package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.request.SourcingGridFieldRequest;
import com.pantavanij.sourcingreq.services.domain.request.SourcingGridFieldSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SourcingGridFieldSequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.SourcingGridFieldSearchResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.PathUrl;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SourcingGridFieldService;
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
public class SourcingGridFieldController {
    private final SourcingGridFieldService sourcingGridFieldService;

    @GetMapping(value = "/sourcing-grid-field")
    public ResponseEntity<ApiResponse<List<SourcingGridFieldDto>>> getSourcingGridField(@RequestParam PathUrl pathUrl) {
        String tenantCode = AppUtil.getTenantId();
        List<SourcingGridFieldDto> sourcingGridFieldDtoList =
                sourcingGridFieldService.getSourcingGridField(pathUrl.privilegeCode(), tenantCode);

        if (sourcingGridFieldDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }

        return ResponseEntity.ok().body(new ApiResponse(sourcingGridFieldDtoList));
    }


    @GetMapping(value = "/sourcing/grid-field/searchable")
    public ResponseEntity<ApiResponse<List<SourcingGridFieldSearchableDto>>> getSourcingGridFieldSearchable(@RequestParam PathUrl pathUrl) {
        String tenantCode = AppUtil.getTenantId();
        List<SourcingGridFieldSearchableDto> sourcingGridFieldDtoList =
                sourcingGridFieldService.getSourcingGridFieldSearchable(pathUrl.privilegeCode(), tenantCode);

        if (sourcingGridFieldDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }

        return ResponseEntity.ok().body(new ApiResponse(sourcingGridFieldDtoList));
    }

//    @PreAuthorize("hasAuthority('SAM')")
//    @PutMapping(value = "/sourcing-grid-field/update", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity updateSourcingGridField(@RequestBody @Valid SourcingGridFieldRequest sourcingGridFieldRequest) {
//        SourcingGridFieldDto sourcingGridFieldDto = sourcingGridFieldService.updateSourcingGridField(sourcingGridFieldRequest);
//        if (sourcingGridFieldDto != null) {
//            return new ResponseEntity<>(new ApiResponse<>(sourcingGridFieldDto, new ApiResponseStatus()), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PreAuthorize("hasAuthority('SAM')")
//    @PutMapping(value = "/sourcing-grid-field/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity updateSourcingGridFieldSequence(@RequestBody @Valid SequenceRequest request) {
//        SourcingGridFieldDto sourcingGridFieldDto = sourcingGridFieldService.updateSourcingGridFieldSequence(request);
//        if (sourcingGridFieldDto != null) {
//            return new ResponseEntity<>(new ApiResponse<>(sourcingGridFieldDto), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @GetMapping(value = "/sourcing-grid-field/cache")
//    public ResponseEntity<ApiResponse<SourcingGridFieldSearchableDto>> getCache() {
//        String tenantCode = AppUtil.getTenantId();
//        List<SourcingGridFieldSearchableDto> sourcingGridFieldDtoList = sourcingGridFieldService.getCache(tenantCode);
//        if (sourcingGridFieldDtoList.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ApiResponse("", new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
//        }
//
//        return ResponseEntity.ok().body(new ApiResponse(sourcingGridFieldDtoList));
//    }


//    @PostMapping(value = "/sourcing-grid-field/cache")
//    public ResponseEntity<ApiResponse<SourcingGridFieldSearchableDto>> refreshCache() {
//        String tenantCode = AppUtil.getTenantId();
//        List<SourcingGridFieldSearchableDto> sourcingGridFieldDtoList = sourcingGridFieldService.refreshCache(tenantCode);
//        if (sourcingGridFieldDtoList.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ApiResponse("", new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
//        }
//        return ResponseEntity.ok().body(new ApiResponse("ok"));
//    }

    @PreAuthorize("hasAuthority('SAM')")
    @ControllerExecuteTime
    @PostMapping(value = "/sourcing-grid-field/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchSourcingGridField(@RequestBody @Valid SourcingGridFieldSearchRequest sourcingGridFieldSearchRequest) {
        Sort sort;
        int page = sourcingGridFieldSearchRequest.getPage();
        int size = sourcingGridFieldSearchRequest.getPageSize();
        String sortBy = sourcingGridFieldSearchRequest.getSortBy();
        String sortOrder = sourcingGridFieldSearchRequest.getSortOrder();

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
        SourcingGridFieldSearchDto sourcingGridFieldSearchDto = sourcingGridFieldService.searchSourcingGridFieldByCondition(sourcingGridFieldSearchRequest, pageable);
        SourcingGridFieldSearchResponse response = SourcingGridFieldSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(sourcingGridFieldSearchDto.getPageSize())
                .page(page)
                .total(sourcingGridFieldSearchDto.getTotal())
                .totalPage(sourcingGridFieldSearchDto.getTotalPage())
                .data(sourcingGridFieldSearchDto.getSourcingGridFields()).build();
        if (sourcingGridFieldSearchDto.getSourcingGridFields() != null && !sourcingGridFieldSearchDto.getSourcingGridFields().isEmpty()) {
            return ResponseEntity.ok().body(response);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/sourcing-grid-field/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewSourcingGridField(@RequestParam("recId") Integer recId, @RequestParam("privilegeCode") String privilegeCode) {
        SourcingGridFieldDto sourcingGridFieldDto = sourcingGridFieldService.findSourcingGridFieldByRecIdAndTenantIdAndPrivilegeCode(recId, privilegeCode);
        if (sourcingGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(sourcingGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/sourcing-grid-field/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createSourcingGridField(@RequestBody @Valid SourcingGridFieldRequest request) {
        SourcingGridFieldDto sourcingGridFieldDto = sourcingGridFieldService.createSourcingGridField(request);
        if (sourcingGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(sourcingGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/sourcing-grid-field/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateSourcingGridField(@RequestBody @Valid SourcingGridFieldRequest request) {
        SourcingGridFieldDto sourcingGridFieldDto = sourcingGridFieldService.updateSourcingGridField(request);
        if (sourcingGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(sourcingGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/sourcing-grid-field/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateSourcingGridFieldSequence(@RequestBody @Valid SourcingGridFieldSequenceRequest request) {
        SourcingGridFieldDto sourcingGridFieldDto = sourcingGridFieldService.updateSourcingGridFieldSequence(request);
        if (sourcingGridFieldDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(sourcingGridFieldDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
