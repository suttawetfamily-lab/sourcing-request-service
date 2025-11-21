package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.dto.DeptApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.RequestStatusOption;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DeptApprovalStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantApprovalStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantRequestStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.TENANT_APPROVAL_DRAFT;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class RequestStatusController {

    private final TenantRequestStatusService tenantRequestStatusService;
    private final TenantApprovalStatusService tenantApprovalStatusService;
    private final TenantService tenantService;
    private final DeptApprovalStatusService deptApprovalStatusService;

    @GetMapping(value = "/request-status/request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getRequestStatusSearchByTenant() {
        List<RequestStatusNameDto> requestStatusNameDtoList = tenantRequestStatusService.getRequestStatusList();

        if (!requestStatusNameDtoList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse(requestStatusNameDtoList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = "/request-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RequestStatusNameDto>>> getRequestStatusSearchBySearchTerm(@RequestParam @SearchTermConstraint String searchTerm) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<OptionDto> requestStatusNameDtoList = tenantRequestStatusService.getRequestStatusOptionDto(tenant.getRecId(), searchTerm.trim());
        if (!requestStatusNameDtoList.isEmpty()) {
            if (searchTerm.isEmpty()) {
                String name = requestStatusNameDtoList.get(0).getName();
                requestStatusNameDtoList = requestStatusNameDtoList.stream()
                        .filter(status -> !status.getName().equals(TENANT_APPROVAL_DRAFT.code()))
                        .collect(Collectors.toList());
                boolean check = name.equals(TENANT_APPROVAL_DRAFT.code());
                System.out.println(check + ":" + name + "!=" + TENANT_APPROVAL_DRAFT.code());
            }
            return ResponseEntity.ok().body(new ApiResponse(requestStatusNameDtoList));

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = "/request-status/option", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getRequestStatusOption() {
        List<OptionDto> requestStatusOptionList = new ArrayList<>();
        for (RequestStatusOption option : RequestStatusOption.values()) {
            requestStatusOptionList.add(new OptionDto(option.getValue().toString(), option.getCode(), option.getDescription(), option.getIsDefault()));
        }

        if (!requestStatusOptionList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse(requestStatusOptionList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = "/dept-approval-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ApprovalStatusNameDto>>> getDepartmentApprovalStatusSearchByTenant() {
        List<DeptApprovalStatusDto> deptApprovalStatusDtoList = deptApprovalStatusService.getDeptApprovalStatusSearchList();

        if (!deptApprovalStatusDtoList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse(deptApprovalStatusDtoList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = "/approval-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ApprovalStatusNameDto>>> getApprovalStatusSearchByTenant() {
        List<ApprovalStatusNameDto> approvalStatusDtoList = tenantApprovalStatusService.getApprovalStatusSearchList();

        if (!approvalStatusDtoList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse(approvalStatusDtoList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = "/request-status/review", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RequestStatusNameDto>>> getReviewStatusSearchByTenant() {
        List<RequestStatusNameDto> requestStatusNameDtoList = tenantRequestStatusService.getReviewStatusList();
        if (!requestStatusNameDtoList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse(requestStatusNameDtoList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

}
