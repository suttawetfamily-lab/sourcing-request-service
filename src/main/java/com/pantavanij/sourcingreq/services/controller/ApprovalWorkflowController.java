package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.DefaultApprovalDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingRequestVisibleConfig;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.ApprovalSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestApproveRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestForwarderRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestRejectRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.SourcingRequestVisibleConfigResponse;
import com.pantavanij.sourcingreq.services.domain.response.approver.ApproverRequestListResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestPurchaserService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.REQUEST_NO;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class ApprovalWorkflowController {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalWorkflowController.class);
    private final RequestService requestService;
    private final RequestPurchaserService requestPurchaserService;

    @PreAuthorize("hasAuthority('SQP')")
    @PostMapping(value = "/approval/request/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity approveRequest(@RequestBody RequestApproveRequest request) {
        try {
            RequestDto isApproved = requestService.approveRequest(request);
            if (isApproved != null) {
                return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1003, ApiMessage.I1003.description()), HttpStatus.OK);
            }
        } catch (Exception e) {
            return checkWorkFlowError(e);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    private ResponseEntity checkWorkFlowError(Exception e) {
        if (e.getMessage().contains("Workflow API error")) {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7029, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E1001, ApiMessage.E1001.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SQP')")
    @PostMapping(value = "/approval/request/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity rejectRequest(@RequestBody RequestRejectRequest request) {
        try {
            RequestDto isRejected = requestService.rejectRequest(request);
            if (isRejected != null) {
                return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1003, ApiMessage.I1003.description()), HttpStatus.OK);
            }
        } catch (Exception e) {
            return checkWorkFlowError(e);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ControllerExecuteTime
    @PreAuthorize("hasAnyAuthority('SQA', 'SQP')")
    @PostMapping(value = "/approval", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchAllApprovalRequest(@RequestBody @Valid ApprovalSearchRequest approvalSearchRequest) {

        int page = approvalSearchRequest.getPage();
        Pageable pageable = BuilderPageable(approvalSearchRequest, page);
        ApproverRequestSearchDto requestSearchDto = requestService.searchAllApprovalListByCondition(approvalSearchRequest, pageable);
        return BuilderApproverRequestListResponse(requestSearchDto, page);
    }

    @ControllerExecuteTime
    @PreAuthorize("hasAnyAuthority('SQA', 'SQP')")
    @PostMapping(value = "/approval/my-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchMyApprovalRequest(@RequestBody @Valid ApprovalSearchRequest approvalSearchRequest) {
        int page = approvalSearchRequest.getPage();
        Pageable pageable = BuilderPageable(approvalSearchRequest, page);
        ApproverRequestSearchDto requestSearchDto = requestService.searchMyApprovalListByCondition(approvalSearchRequest, pageable);
        return BuilderApproverRequestListResponse(requestSearchDto, page);
    }

//    @PreAuthorize("hasAnyAuthority('SQA', 'SQP')")
//    @GetMapping(value = "tenant-config/approval", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<SourcingRequestVisibleConfigResponse> hideAllApprovalRequest() {
//        SourcingRequestVisibleConfig sourcingRequestVisibleConfig = requestPurchaserService.getApprovalAllTapConfig();
//        return new ResponseEntity<>(new SourcingRequestVisibleConfigResponse(sourcingRequestVisibleConfig), HttpStatus.OK);
//    }

    @PostMapping(value = "/approval/forward", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> forwardApproval(@RequestBody RequestForwarderRequest forwarderRequest) {
        logger.info("approveRequest requestId : {} ", forwarderRequest.getRequestId());
        try {
            boolean response = requestService.forwardApproval(forwarderRequest);
            if(response) {
                return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E1003, ApiMessage.E1003.description()), HttpStatus.NOT_FOUND);
            }
     } catch (Exception e) {
            if (e.getMessage().contains("Workflow API error")) {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7029, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E1001, ApiMessage.E1001.description()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping(value = "/approval/defaultapproval", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDefaultApproval(@RequestParam("erfxid") String erfxid,
                                             @RequestParam("amount") Double amount) {
        logger.info("getReportLineApproval erfxid : {} ", erfxid);
        try {
            List<DefaultApprovalDto> defaultApproval = requestService.getDefaultApproval(erfxid, amount);
            return new ResponseEntity<>(new ApiResponse<>(defaultApproval), HttpStatus.OK);
        } catch (Exception e) {
            if (e.getMessage().contains("Workflow API error")) {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7029, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E1001, ApiMessage.E1001.description()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private Pageable BuilderPageable(ApprovalSearchRequest approvalSearchRequest, int page) {
        Sort sort;
        int size = approvalSearchRequest.getPageSize();
        String sortBy = approvalSearchRequest.getSortBy();
        String sortOrder = approvalSearchRequest.getSortOrder();

        if ("".equals(sortOrder) || sortOrder == null && "".equals(sortBy) || sortBy == null) {
            sort = Sort.by(REQUEST_NO.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortOrder != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            if (!sortBy.equalsIgnoreCase(REQUEST_NO.description())) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, REQUEST_NO.description()));
            }
            sort = Sort.by(orders);
        }
        return PageRequest.of(page - 1, size, sort);
    }

    private ResponseEntity BuilderApproverRequestListResponse(ApproverRequestSearchDto requestSearchDto, int page) {
        ApproverRequestListResponse response = ApproverRequestListResponse.builder()
                .pageSize(requestSearchDto.getPageSize())
                .page(page)
                .total(requestSearchDto.getTotal())
                .totalPage(requestSearchDto.getTotalPage())
                .data(requestSearchDto.getRequestDtoList()).build();

        if (requestSearchDto.getRequestDtoList() != null && !requestSearchDto.getRequestDtoList().isEmpty()) {
            return ResponseEntity.ok().body(response);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }
}
