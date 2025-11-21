package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_UNKNOWN;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.REQUEST_NO;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class RequestDeptApproverController {

    private final RequestDeptApproverService requestDeptApproverService;
    private final ExistingPriceItemService existingPriceItemService;
    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final RequestItemService requestItemService;
    private final SourcingStatusService sourcingStatusService;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final RequestReviewerRepository requestReviewerRepository;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final DelegationService delegationService;
    private final RequestReportLineRepository requestReportLineRepository;
    private final RequestApproverRepository requestApproverRepository;


    @PreAuthorize("hasAuthority('SQA')")
    @PostMapping(value = "/request-dept-approver/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity approveRequest(@RequestBody RequestDeptApprovalRequest request) {
        try {
            boolean isApproved = requestService.approveRequest(request);
            if (isApproved) {
                return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1003, ApiMessage.I1003.description()), HttpStatus.OK);
            }
        } catch (Exception e) {
            //return checkWorkFlowError(e);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PreAuthorize("hasAuthority('SQA')")
    @PostMapping(value = "/request-dept-approver/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity rejectRequest(@RequestBody RequestDeptApprovalRequest request) {
        try {
            boolean isRejected = requestService.rejectRequest(request);
            if (isRejected) {
                return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1003, ApiMessage.I1003.description()), HttpStatus.OK);
            }
        } catch (Exception e) {
            //return checkWorkFlowError(e);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @GetMapping(value = "/request-dept-approver/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRequestDeptApprover(@PathVariable Long requestId) {
        UserDto user = AppUtil.getUser();
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        if (AppUtil.isAllowedAction(user, request, requestPurchasers, requestApprovers, null, null, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_UNKNOWN)) {
            EPAuthDeptApproverResponse response = this.getEpAuthDeptApprover(requestId);
            List<RequestDeptApproverDto> requestDeptApproverDtos = requestDeptApproverService.findByRequest(requestId);
            List<EPAuthDeptApproverDto> epAuthDeptApproverList = response.getData().stream().filter(r -> requestDeptApproverDtos.stream().anyMatch(req -> req.getApproverName().equals(r.getLoginId().toString())))
                    .map(item -> {
                        RequestDeptApproverDto requestDeptApproverDto = requestDeptApproverDtos.stream()
                                .filter(req -> req.getApproverName().equals(item.getLoginId().toString()) && req.getRequest().getRecId().equals(requestId))
                                .findFirst().orElse(null);
                        if (requestDeptApproverDto != null) {
                            EPAuthDeptApproverDto epAuthDeptApproverDto = new EPAuthDeptApproverDto(); // EPAuthReviewerDto.builder()
                            epAuthDeptApproverDto.setRequestDeptApproverId(requestDeptApproverDto.getRecId());
                            epAuthDeptApproverDto.setSysUserId(item.getSysUserId());
                            epAuthDeptApproverDto.setLoginId(item.getLoginId());
                            epAuthDeptApproverDto.setFullName(item.getFullName());
                            epAuthDeptApproverDto.setEmail(item.getEmail());
                            epAuthDeptApproverDto.setMobilePhone(item.getMobilePhone());
                            epAuthDeptApproverDto.setPhone(item.getPhone());
                            epAuthDeptApproverDto.setTimezone(item.getTimezone());
                            epAuthDeptApproverDto.setComment(requestDeptApproverDto.getComment());
                            epAuthDeptApproverDto.setCommentDate(!"-".equalsIgnoreCase(requestDeptApproverDto.getComment()) ? requestDeptApproverDto.getUpdatedDate() : null);
                            epAuthDeptApproverDto.setAddedBy(UserDetailServiceUtil.getFullName(request.getCreatedBy()));
                            return epAuthDeptApproverDto;
                        } else {
                            return null;
                        }
                    })
                    .distinct()
                    .collect(Collectors.toList());

            response.setData(epAuthDeptApproverList);
            response.setTotal(requestDeptApproverDtos.size());
            return ResponseEntity.ok().body(response);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @ControllerExecuteTime
    @PostMapping(value = "/request-dept-approver/search-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequestDeptApprover(@RequestBody @Valid DeptApproverSearchRequest deptApproverSearchRequest) {
        EPAuthDeptApproverResponse response = requestDeptApproverService.getDeptApproverListByConditions(deptApproverSearchRequest);
        if (response.getData() != null && !response.getData().isEmpty()) {
            List<EPAuthDeptApproverDto> epAuthDeptApproverList =
                    response.getData().stream().filter(
                            i -> !deptApproverSearchRequest.getExceptApprovers().contains(i.getSysUserId().toString()))
                            .distinct()
                            .collect(Collectors.toList());
            response.setData(epAuthDeptApproverList);
            response.setTotal(epAuthDeptApproverList.size());
            return ResponseEntity.ok().body(response);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

//    @PreAuthorize("hasAnyAuthority('SQV','SRL')")
//    @PostMapping(value = "/request-dept-approver/comment")
//    public ResponseEntity addCommentRequestReviewer(@Valid @RequestBody ReviewerCommentRequest reviewerCommentRequest) {
//        List<RequestReviewerDto> requestReviewerDtos = requestReviewerService.saveCommentReviewer(reviewerCommentRequest);
//        if(requestReviewerDtos != null && !requestReviewerDtos.isEmpty()) {
//            return new ResponseEntity<>(new RequestReviewerResponse(requestReviewerDtos), HttpStatus.OK);
//        }else{
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()) , HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping(value = {"/request-dept-approver/qualified", "/request-dept-approver/{authCode}/qualified"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRequestDeptApproverQualifiedList(@PathVariable Optional<String> authCode, @Valid @RequestBody RequestReviewerSourcingStatusRequest request) {
        ExistingPriceItemListResponse existingPriceItemListResponse;
        List<RequestItemDto> requestItemLst = requestItemService.getRequestItemByRequestIdAndSourcingStatusId(request.getRequestId(), request.getSourcingStatusId());

        if (!requestItemLst.isEmpty()) {
            Sort sort = Sort.by("recId").ascending();
            Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
            Request requestOpt = requestService.searchRequestByRecId(request.getRequestId());
            SourcingStatus sourcingStatusOpt = sourcingStatusService.getSourcingStatusById(request.getSourcingStatusId());
            ExistingPriceItemSearchDto existingPriceItemSearchDto = existingPriceItemService.getItemByRequestAndSourcingStatus(requestOpt, sourcingStatusOpt, pageable, authCode.orElse(""));
            existingPriceItemListResponse = ExistingPriceItemListResponse.builder()
                    .data(existingPriceItemSearchDto.getExistingPriceItemDtoList())
                    .page(request.getPage())
                    .pageSize(request.getSize())
                    .total(existingPriceItemSearchDto.getTotal())
                    .totalPage(existingPriceItemSearchDto.getTotalPage())
                    .build();
            return ResponseEntity.ok().body(existingPriceItemListResponse);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @ControllerExecuteTime
    @PostMapping(value = "/request-dept-approver/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequestDeptApprover(@RequestBody @Valid RequestDeptApproverSearchRequest requestDeptApproverSearchRequest) {
        Sort sort;
        int page = requestDeptApproverSearchRequest.getPage();
        int size = requestDeptApproverSearchRequest.getPageSize();
        String sortBy = requestDeptApproverSearchRequest.getSortBy();
        String sortOrder = requestDeptApproverSearchRequest.getSortOrder();

        if ("".equals(sortOrder) || sortOrder == null && "".equals(sortBy) || sortBy == null) {
            sort = Sort.by(REQUEST_NO.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortOrder != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            if(!sortBy.equalsIgnoreCase(REQUEST_NO.description())) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, REQUEST_NO.description()));
            }
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        List<Long> requestIds = requestDeptApproverService.findAllRelatedRequestIds();

        if (requestIds != null && !requestIds.isEmpty()) {
            RequestDeptApproverSearchDto requestDeptApproverSearchDto = requestService.searchRequestDeptApproverByCondition(requestDeptApproverSearchRequest, requestIds, pageable);

            RequestDeptApproverListResponse response = RequestDeptApproverListResponse.builder()
                    .pageSize(requestDeptApproverSearchDto.getPageSize())
                    .page(page)
                    .total(requestDeptApproverSearchDto.getTotal())
                    .totalPage(requestDeptApproverSearchDto.getTotalPage())
                    .data(requestDeptApproverSearchDto.getRequestDtoList()).build();
            if (requestDeptApproverSearchDto.getRequestDtoList() != null && !requestDeptApproverSearchDto.getRequestDtoList().isEmpty()) {
                return ResponseEntity.ok().body(response);
            } else {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    private EPAuthDeptApproverResponse getEpAuthDeptApprover (Long requestId) {
        DeptApproverSearchRequest deptApproverSearchRequest = new DeptApproverSearchRequest();
        deptApproverSearchRequest.setTenantId(AppUtil.getTenantId());
        deptApproverSearchRequest.setRequestId(requestId);
        deptApproverSearchRequest.setPage(1);
        deptApproverSearchRequest.setPageSize(99999);// Checking passed
        deptApproverSearchRequest.setSortBy("username");
        deptApproverSearchRequest.setSortOrder("desc");
        return requestDeptApproverService.getDeptApproverListByConditions(deptApproverSearchRequest);
    }
}
