package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class SourcingApproverController {

    private final ExistingPriceItemService existingPriceItemService;
    private final RequestService requestService;
    private final ExcSourcingService excSourcingService;
    private final RequestItemService requestItemService;
    private final SourcingStatusService sourcingStatusService;

    @PostMapping(value = "/request-sourcing-approver/approve", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/request-sourcing-approver/reject", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = {"/request-sourcing-approver/qualified", "/request-sourcing-approver/{authCode}/qualified"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRequestSourcingApproverQualifiedList(@PathVariable Optional<String> authCode, @Valid @RequestBody RequestReviewerSourcingStatusRequest request) {
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
    @PostMapping(value = "/sourcing-approver/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchExcSourcingApprover(@RequestBody @Valid ExcSourcingApproverSearchRequest searchRequest) {
        Sort sort;
        int page = searchRequest.getPage();
        int size = searchRequest.getPageSize();
        String sortBy = searchRequest.getSortBy();
        String sortOrder = searchRequest.getSortOrder();

        // mapping sort field ให้ตรง entity
        String sortByResolved = resolveSortBy(sortBy);

        if (sortOrder == null || sortOrder.isEmpty() || sortByResolved == null || sortByResolved.isEmpty()) {
            sort = Sort.by("excSourcingDocNo").descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            orders.add(new Sort.Order(
                    sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    sortByResolved
            ));
            if (!"excSourcingDocNo".equalsIgnoreCase(sortByResolved)) {
                orders.add(new Sort.Order(
                        sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                        "excSourcingDocNo"
                ));
            }
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        ExcSourcingApproverSearchDto dto = excSourcingService.searchExcSourcingApproverByCondition(searchRequest, pageable);

        ExcSourcingApproverListResponse response = ExcSourcingApproverListResponse.builder()
                .pageSize(dto.getPageSize())
                .page(page)
                .total(dto.getTotal())
                .totalPage(dto.getTotalPage())
                .data(dto.getExcSourcingDtoList())
                .build();

        if (dto.getExcSourcingDtoList() != null && !dto.getExcSourcingDtoList().isEmpty()) {
            return ResponseEntity.ok().body(response);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }

    }

    private String resolveSortBy(String sortBy) {
        if (sortBy == null) return null;
        switch (sortBy.toLowerCase()) {
            case "requestno":
                return "request.requestNo";
            case "requestname":
                return "request.requestName";
            case "excSourcingdocno":
                return "excSourcingDocNo";
            case "createddate":
                return "createdDate";
            default:
                return sortBy; // default ใช้ตรง ๆ
        }
    }
}
