package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.ReviewerRequestDto;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.domain.response.requester.RequesterRequestResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.Role.REPORT_LINE;
import static com.pantavanij.sourcingreq.services.enums.Role.REVIEWER;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.REQUEST_NO;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class RequestController {
    private final RequestReviewerService requestReviewerService;
    private final RequestService requestService;
    private final ExcelService excelService;
    private final UaaService uaaService;

    @GetMapping(value = "/request/approvers")
    public ResponseEntity getDefaultApprovers() {
        DefaultApproverDto defaultApproverDto = requestService.getDefaultApprovers();
        return new ResponseEntity<>(new DefaultApproverResponse(defaultApproverDto), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('SQN')")
    @ControllerExecuteTime
    @GetMapping(value = "/request/{requestId}/view")
    public ResponseEntity<ApiResponse<RequesterRequestDto>> getRequestView(@PathVariable Long requestId) {
        RequesterRequestDto requestDto = requestService.findRequestSourcingByRecId(requestId);
        if (requestDto != null) {
            return new ResponseEntity<>(new ApiResponse(requestDto), HttpStatus.OK);
        }
        return new ResponseEntity(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @ControllerExecuteTime
    @GetMapping(value = "/request/{requestId}/approval")
    public ResponseEntity<ApiResponse<ApproverRequestDto>> getRequestApproval(@PathVariable Long requestId) {
        ApproverRequestDto requestDto = requestService.findRequestApprovalByRecId(requestId);
        if (requestDto != null) {
            requestDto.setTags("it");
            return new ResponseEntity<>(new ApiResponse(requestDto), HttpStatus.OK);
        } else {
            return new ResponseEntity(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }
    
    @GetMapping(value = "/request/{requestId}/review")
    public ResponseEntity<ApiResponse<ReviewerRequestDto>> getRequestReview(@PathVariable Long requestId) {
        ReviewerRequestDto requestDto = requestService.findRequestReviewerByRecId(requestId);
        if (requestDto != null) {
            // TODO: Stamp 'Read' field to be 1
            return new ResponseEntity<>(new ApiResponse(requestDto), HttpStatus.OK);
        }
        return new ResponseEntity(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP')")
    @GetMapping(value = "/request/{requestId}/edit")
    public ResponseEntity<RequestDto> getRequestEdit(@PathVariable Long requestId) {
        RequestDto requestDto = requestService.findRequestSourcingForEditingByRecId(requestId);
        if (requestDto != null) {
            return new ResponseEntity(new RequestResponse(requestDto), HttpStatus.OK);
        }
        return new ResponseEntity(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PreAuthorize("hasAuthority('SQN')")
    @GetMapping(value = "/request/{requestId}/duplicate")
    public ResponseEntity getRequestDuplicate(@PathVariable Long requestId) {
        RequestDto requestDto = requestService.findByRecIdForDuplicate(requestId);
        if (requestDto != null) {
            return new ResponseEntity<>(new RequestResponse(requestDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP')")
    @PostMapping(value = "/request/{requestId}/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity validateRequest(@PathVariable Long requestId) {
        boolean result = requestService.validateRequest(requestId);
        if (result) {
            return new ResponseEntity<>(new ApiResponse(result), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7088, ApiMessage.E7088.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ControllerExecuteTime
    @PreAuthorize("hasAuthority('SQN')")
    @PostMapping(value = "/request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDto> saveRequest(@Valid @RequestBody RequestRequest request) {
        String[] privilegeCodeList = new String[] {REVIEWER.privilegeCode(), REPORT_LINE.privilegeCode()};
        EPAuthReviewerResponse response = getEpAuthReviewer(request.getRecId(), privilegeCodeList);
        RequestDto requestDto = requestService.saveRequest(request, response,true);
        if (requestDto != null) {
            return new ResponseEntity(new RequestResponse(requestDto), HttpStatus.OK);
        }
        return new ResponseEntity(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ControllerExecuteTime
    @PreAuthorize("hasAnyAuthority('SQN','SQP')")
    @PostMapping(value = "/request/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDto> saveRequestToAwaitingApproval(@RequestBody RequestRequest request) {
        String[] privilegeCodeList = new String[] {REVIEWER.privilegeCode(), REPORT_LINE.privilegeCode()};
        EPAuthReviewerResponse response = getEpAuthReviewer(request.getRecId(), privilegeCodeList);
        RequestDto requestDto = requestService.saveRequest(request, response, false);
        if (requestDto != null) {
            return new ResponseEntity(new RequestResponse(requestDto), HttpStatus.OK);
        }
        return new ResponseEntity(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PreAuthorize("hasAuthority('SQN')")
    @PostMapping(value = "/request/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity cancelRequest(@RequestBody RequestCancellationRequest request) {
        RequestDto requestDto = requestService.cancelRequest(request);
        if (requestDto != null) {
            return new ResponseEntity<>(new RequestResponse(requestDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PreAuthorize("hasAuthority('SQN')")
    @DeleteMapping(value = "/request/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteRequest(@PathVariable Long requestId) {
        if (requestService.deleteRequestByRecId(requestId)) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1004, ApiMessage.I1004.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7033, ApiMessage.E7033.description()), HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasAuthority('SQP')")
    @PostMapping(value = "/request/{requestId}/assign", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity assignRequest(@PathVariable Long requestId) {
        boolean result = requestService.assignRequestByRecId(requestId);
        if (result) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1003, ApiMessage.I1003.description()), HttpStatus.OK);
        } else {
            String tenantId = AppUtil.getTenantId();
            String idp = AppUtil.getIdp();
            RequesterRequestDto requestDto = requestService.findRequestSourcingByRecId(requestId);
            ContractDetailClientDto contractDetailClientDto = null;
            if(requestDto != null) {
                contractDetailClientDto = uaaService.getContractDetail(tenantId, idp, requestDto.getAssignedBy(), null);
            }
            return new ResponseEntity<>(new RequestAssignResponse(contractDetailClientDto, ApiMessage.E7037, ApiMessage.E7037.description()), HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasAuthority('SQP')")
    @PostMapping(value = "/request/{requestId}/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity removeRequest(@PathVariable Long requestId) {
        if (requestService.removeRequestByRecId(requestId)) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1003, ApiMessage.I1003.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7038, ApiMessage.E7038.description()), HttpStatus.CONFLICT);
        }
    }

    @ControllerExecuteTime
    @PostMapping(value = "/request/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchRequest(@RequestBody @Valid RequestSearchRequest requestSearchRequest) {
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
            if (!sortBy.equalsIgnoreCase(REQUEST_NO.description())) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, REQUEST_NO.description()));
            }
            sort = Sort.by(orders);
        }
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        RequesterRequestSearchDto requestSearchDto = requestService.searchRequestByCondition(requestSearchRequest, pageable);
        RequesterRequestResponse response = RequesterRequestResponse.builder()
                .pageSize(requestSearchDto.getPageSize())
                .page(page)
                .total(requestSearchDto.getTotal())
                .totalPage(requestSearchDto.getTotalPage())
                .data(RequestMapper.INSTANCE.toRequesterRequestSearchDataDto(requestSearchDto.getRequestDtoList())).build();
        if (requestSearchDto.getRequestDtoList() != null && !requestSearchDto.getRequestDtoList().isEmpty()) {
            return ResponseEntity.ok().body(response);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @ControllerExecuteTime
    @PreAuthorize("hasAnyAuthority('SSR')")
    @PostMapping(value = "/request/export/{requestReportId}", produces = "application/vnd.ms-excel")
    public ResponseEntity exportRequest(
            @RequestBody RequestSearchRequest searchRequest,
            @PathVariable Integer requestReportId,
            @RequestParam(value = "organizationId", required = false) Integer organizationId
    ) throws IOException {
        if (searchRequest.getConditionSearchList().stream().noneMatch(s -> s.getSearchValue().length() > 0
                && s.getSearchValue().length() < 3)) {
            // Export Excel results
            List<RequesterRequestDto> retuestList = requestService.searchRequestExcelByCondition(searchRequest);
            String fileName = excelService.generateRequestReport(retuestList, requestReportId, organizationId);

            MediaType mediaType = MediaType.parseMediaType("application/vnd.ms-excel");
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteArrayResource resource = new ByteArrayResource(data);

            // Delete File
            FileUtil.deleteFile(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                    .contentType(mediaType)
                    .contentLength(data.length)
                    .body(resource);
        }
        MediaType mediaType = MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorResponse status = new ApiErrorResponse(ApiMessage.E7034, ApiMessage.E7034.description());

        return ResponseEntity.internalServerError()
                .contentType(mediaType)
                .body(status);
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
