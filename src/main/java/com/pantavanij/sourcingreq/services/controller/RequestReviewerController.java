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
import static com.pantavanij.sourcingreq.services.enums.Role.REVIEWER;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.REQUEST_NO;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class RequestReviewerController {

    private final RequestReviewerService requestReviewerService;
    private final ExistingPriceItemService existingPriceItemService;
    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final RequestItemService requestItemService;
    private final SourcingStatusService sourcingStatusService;
    private final UaaService uaaService;
    private final RequestReportLineService requestReportLineService;
    private final ReportLineService reportLIneService;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final RequestReviewerRepository requestReviewerRepository;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final DelegationService delegationService;
    private final RequestReportLineRepository requestReportLineRepository;
    private final RequestApproverRepository requestApproverRepository;

//    @PostMapping(value = "/request-reviewer", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity addRequestReviewer(@Valid @RequestBody RequestReviewerRequest requestReviewerRequest){
//        EPAuthReviewerResponse response = getEpAuthReviewer(requestReviewerRequest.getRequestId());
//        List<RequestReviewerDto> requestReviewerDtos = requestReviewerService.saveRequestReviewer(requestReviewerRequest, response);
//        if(requestReviewerDtos != null) {
//            return new ResponseEntity<>(new RequestReviewerResponse(requestReviewerDtos), HttpStatus.OK);
//        }else{
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()) , HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @GetMapping(value = "/request-reviewer/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRequestReviewer(@PathVariable Long requestId) {
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
            String[] privilegeCodeList = new String[] {REVIEWER.privilegeCode()};
            EPAuthReviewerResponse response = getEpAuthReviewer(requestId, privilegeCodeList);
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            List<RequestReviewerDto> requestReviewerDtos = requestReviewerService.findByRequest(requestId);
            List<EPAuthReviewerDto> epAuthReviewerList = response.getData().stream()
                    .filter(r -> requestReviewerDtos.stream().anyMatch(req -> req.getReviewer().getLoginId().equalsIgnoreCase(r.getLoginId().toString())))
                    .map(item -> {
                        RequestReviewerDto requestReviewerDto = requestReviewerDtos.stream()
                                .filter(req -> req.getReviewer().getLoginId().equalsIgnoreCase(item.getLoginId().toString()) && req.getRequest().getRecId().equals(requestId))
                                .findFirst().orElse(null);
                        if (requestReviewerDto != null) {
                            EPAuthReviewerDto epAuthReviewerDto = new EPAuthReviewerDto(); // EPAuthReviewerDto.builder()
                            epAuthReviewerDto.setRequestReviewerId(requestReviewerDto.getRecId());
                            epAuthReviewerDto.setSysUserId(item.getSysUserId());
                            epAuthReviewerDto.setLoginId(item.getLoginId());
                            epAuthReviewerDto.setFullName(item.getFullName());
                            epAuthReviewerDto.setEmail(item.getEmail());
                            epAuthReviewerDto.setMobilePhone(item.getMobilePhone());
                            epAuthReviewerDto.setPhone(item.getPhone());
                            epAuthReviewerDto.setTimezone(item.getTimezone());
                            epAuthReviewerDto.setComment(requestReviewerDto.getComment());
                            epAuthReviewerDto.setCommentDate(!"-".equalsIgnoreCase(requestReviewerDto.getComment()) ? requestReviewerDto.getUpdatedDate() : null);
                            epAuthReviewerDto.setAddedBy(UserDetailServiceUtil.getFullName(request.getCreatedBy()));
                            return epAuthReviewerDto;
                        } else {
                            return null;
                        }
                    })
                    .distinct()
                    .collect(Collectors.toList());

            response.setData(epAuthReviewerList);
            response.setTotal(requestReviewerDtos.size());
            return ResponseEntity.ok().body(response);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

//    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
//    @ControllerExecuteTime
//    @PostMapping(value = "/request-reviewer/search", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity searchRequest(@RequestBody @Valid ReviewerSearchRequest reviewerSearchRequest) {
//        String[] privilegeCodeList = new String[] {REVIEWER.privilegeCode()};
//        EPAuthReviewerResponse response = requestReviewerService.getReviewerListByConditions(reviewerSearchRequest, privilegeCodeList);
//        if (response.getData() != null && !response.getData().isEmpty()) {
//            List<EPAuthReviewerDto> epAuthReviewerList =
//                    response.getData().stream().filter(
//                            i -> !reviewerSearchRequest.getExceptReviewers().contains(i.getSysUserId().toString()))
//                            .distinct()
//                            .collect(Collectors.toList());
//            response.setData(epAuthReviewerList);
//            response.setTotal(epAuthReviewerList.size());
//            return ResponseEntity.ok().body(response);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
//        }
//    }

    @PreAuthorize("hasAnyAuthority('SQV','SRL')")
    @PostMapping(value = "/request-reviewer/comment")
    public ResponseEntity addCommentRequestReviewer(@Valid @RequestBody ReviewerCommentRequest reviewerCommentRequest) {
        List<RequestReviewerDto> requestReviewerDtos = requestReviewerService.saveCommentReviewer(reviewerCommentRequest);
        if(requestReviewerDtos != null && !requestReviewerDtos.isEmpty()) {
            return new ResponseEntity<>(new RequestReviewerResponse(requestReviewerDtos), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()) , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = {"/request-reviewer/qualified", "/request-reviewer/{authCode}/qualified"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRequestReviewerQualifiedList(@PathVariable Optional<String> authCode, @Valid @RequestBody RequestReviewerSourcingStatusRequest request) {
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
    @PostMapping(value = "/request-reviewer/search-viewer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequestViewer(@RequestBody @Valid RequestSearchRequest requestSearchRequest) {
        Sort sort;
        int page = requestSearchRequest.getPage();
        int size = requestSearchRequest.getPageSize();
        String sortBy = requestSearchRequest.getSortBy();
        String sortOrder = requestSearchRequest.getSortOrder();

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
//        List<Long> requestId = null;

        Map<String, UserDetailResponse> userDetailMap = new HashMap<>();
//        ContractDetailClientDto userDetail = uaaService.getContractDetail(AppUtil.getTenantId(), AppUtil.getIdp(), AppUtil.getUserName(), userDetailMap);
//        requestId = requestReviewerService.findRequestReviewerByReviewerName(userDetail.getUsername());
//        List<ReportLine> reportLine = reportLIneService.findByReportLineName(userDetail.getFirstName() + " " + userDetail.getLastName());
//        if (!reportLine.isEmpty()) {
//            List<Long> reportLineIds = reportLine.stream().map(ReportLine::getRecId).collect(Collectors.toList());
//            List<Long> reqReportLineId = requestReportLineService.findRequestByReportLineId(reportLineIds);
//            if (!reqReportLineId.isEmpty()) {
//                requestId.addAll(reqReportLineId);
//            }
//        }

//        if (requestId != null && !requestId.isEmpty()) {
            RequestSearchDto requestSearchDto = requestService.searchRequestReviewerByCondition(requestSearchRequest, pageable);

            RequestListResponse response = RequestListResponse.builder()
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
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
//        }
    }

    private EPAuthReviewerResponse getEpAuthReviewer (Long requestId, String[] privilegeCodes) {
        ReviewerSearchRequest reviewerSearchRequest = new ReviewerSearchRequest();
        reviewerSearchRequest.setTenantId(AppUtil.getTenantId());
        reviewerSearchRequest.setRequestId(requestId);
        reviewerSearchRequest.setPage(1);
        reviewerSearchRequest.setPageSize(99999);// Checking passed
        reviewerSearchRequest.setSortBy("username");
        reviewerSearchRequest.setSortOrder("desc");
        return requestReviewerService.getReviewerListByConditions(reviewerSearchRequest, privilegeCodes);
    }
}
