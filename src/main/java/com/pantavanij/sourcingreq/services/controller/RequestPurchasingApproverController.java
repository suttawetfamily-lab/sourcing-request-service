package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class RequestPurchasingApproverController {

    private final RequestPurchasingApproverService requestPurchasingApproverService;
    private final RequestService requestService;




    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @ControllerExecuteTime
    @PostMapping(value = "/request-purchasing-approver/search-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequestDeptApprover(@RequestBody @Valid DeptApproverSearchRequest deptApproverSearchRequest) {
        EPAuthDeptApproverResponse response = requestPurchasingApproverService.getPurchasingApproverListByConditions(deptApproverSearchRequest);
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



//    @ControllerExecuteTime
//    @PostMapping(value = "/request-dept-approver/search", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity searchRequestDeptApprover(@RequestBody @Valid RequestDeptApproverSearchRequest requestDeptApproverSearchRequest) {
//        Sort sort;
//        int page = requestDeptApproverSearchRequest.getPage();
//        int size = requestDeptApproverSearchRequest.getPageSize();
//        String sortBy = requestDeptApproverSearchRequest.getSortBy();
//        String sortOrder = requestDeptApproverSearchRequest.getSortOrder();
//
//        if ("".equals(sortOrder) || sortOrder == null && "".equals(sortBy) || sortBy == null) {
//            sort = Sort.by(REQUEST_NO.description()).descending();
//        } else {
//            List<Sort.Order> orders = new ArrayList<>();
//            assert sortOrder != null;
//            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
//            if(!sortBy.equalsIgnoreCase(REQUEST_NO.description())) {
//                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, REQUEST_NO.description()));
//            }
//            sort = Sort.by(orders);
//        }
//
//        Pageable pageable = PageRequest.of(page - 1, size, sort);
//        List<Long> requestIds = requestDeptApproverService.findAllRelatedRequestIds();
//
//        if (requestIds != null && !requestIds.isEmpty()) {
//            RequestDeptApproverSearchDto requestDeptApproverSearchDto = requestService.searchRequestDeptApproverByCondition(requestDeptApproverSearchRequest, requestIds, pageable);
//
//            RequestDeptApproverListResponse response = RequestDeptApproverListResponse.builder()
//                    .pageSize(requestDeptApproverSearchDto.getPageSize())
//                    .page(page)
//                    .total(requestDeptApproverSearchDto.getTotal())
//                    .totalPage(requestDeptApproverSearchDto.getTotalPage())
//                    .data(requestDeptApproverSearchDto.getRequestDtoList()).build();
//            if (requestDeptApproverSearchDto.getRequestDtoList() != null && !requestDeptApproverSearchDto.getRequestDtoList().isEmpty()) {
//                return ResponseEntity.ok().body(response);
//            } else {
//                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
//            }
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
//        }
//    }
//
//    private EPAuthDeptApproverResponse getEpAuthDeptApprover (Long requestId) {
//        DeptApproverSearchRequest deptApproverSearchRequest = new DeptApproverSearchRequest();
//        deptApproverSearchRequest.setTenantId(AppUtil.getTenantId());
//        deptApproverSearchRequest.setRequestId(requestId);
//        deptApproverSearchRequest.setPage(1);
//        deptApproverSearchRequest.setPageSize(99999);// Checking passed
//        deptApproverSearchRequest.setSortBy("username");
//        deptApproverSearchRequest.setSortOrder("desc");
//        return requestDeptApproverService.getDeptApproverListByConditions(deptApproverSearchRequest);
//    }
}
