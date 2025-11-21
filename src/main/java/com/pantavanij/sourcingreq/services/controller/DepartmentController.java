package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DepartmentService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
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

import static com.pantavanij.sourcingreq.services.enums.SearchDepartmentType.*;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
@Validated
public class DepartmentController {
    private final DepartmentService departmentService;
    private final TenantService tenantService;

    @GetMapping(value = "/department", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DepartmentOptionDto>>> getDepartmentBySearchTerm(@RequestParam @SearchTermConstraint String searchTerm, @RequestHeader(value = "organization", required = false) Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());


        List<DepartmentOptionDto> projectOptions = departmentService.getDepartmentBySearchTerm(tenant.getRecId(), searchTerm, organizationId);
        return ResponseEntity.ok().body(new ApiResponse(projectOptions));
    }

    @PreAuthorize("hasAuthority('SMD')")
    @GetMapping(value = "/department/view/{departmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewDepartment(@PathVariable ("departmentId") Integer departmentId) {
        DepartmentDto departmentDto = departmentService.findDepartmentByRecIdAndTenantId(departmentId);
        if (departmentDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(departmentDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/department", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDepartmentBySearchTermV1(@RequestParam @SearchTermConstraint String searchTerm, @RequestHeader(value = "organization", required = false) Integer organizationId) {
        List<DepartmentDto> departmentList = departmentService.getDepartmentBySearchTerm(searchTerm, organizationId);
        if (!departmentList.isEmpty()) {
            return ResponseEntity.ok().body(new DepartmentResponse(departmentList));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiErrorResponse(ApiMessage.E7025, ApiMessage.E7025.description()));
        }
    }

    @PreAuthorize("hasAuthority('SMD')")
    @PostMapping(value = "/department/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createDepartment(@RequestBody @Valid DepartmentRequest request) {
        DepartmentDto departmentDto = departmentService.createDepartment(request);
        if (departmentDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(departmentDto), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMD')")
    @PostMapping(value = "/department/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchDepartment(@RequestBody @Valid DepartmentSearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null && "".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(NAME.description()).descending();
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
        DepartmentSearchDto departmentSearchDto = departmentService.searchDepartmentByCondition(request, pageable);
        DepartmentSearchResponse response = DepartmentSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(departmentSearchDto.getPageSize())
                .page(page)
                .totalPage(departmentSearchDto.getTotalPage())
                .total(departmentSearchDto.getTotal())
                .data(departmentSearchDto.getDepartmentList())
                .build();

        if (departmentSearchDto.getDepartmentList() != null && !departmentSearchDto.getDepartmentList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMD')")
    @PutMapping(value = "/department/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDepartment(@RequestBody @Valid DepartmentRequest request) {
        DepartmentDto departmentDto = departmentService.updateDepartment(request);
        if (departmentDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(departmentDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMD')")
    @PutMapping(value = "/department/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDepartmentSequence(@RequestBody @Valid DepartmentSequenceRequest request) {
        DepartmentDto departmentDto = departmentService.updateDepartmentSequence(request);
        if (departmentDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(departmentDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMD')")
    @DeleteMapping(value = "/department/{departmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteDepartment(@PathVariable("departmentId") Integer departmentId) {
        DepartmentDto departmentDto = departmentService.findDepartmentByRecIdAndTenantId(departmentId);
        if (departmentDto != null) {
            int result = departmentService.deleteDepartmentByRecId(departmentId);
            if (result == 1) {
                return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
            } else if(result == -1) {
                return new ResponseEntity<>(new ApiResponseStatus(
                        ApiMessage.E7100,
                        String.format(ApiMessage.E7100.description(), "This department \"" + departmentDto.getName() + "\" cannot be delete because the resource is referenced by other entities.")), HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
