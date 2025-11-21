package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.version}")
public class ApproverController {

    private final ApproverService approverService;

    @PreAuthorize("hasAuthority('SMA')")
    @GetMapping(value = "/approver/view/{approverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewApprover(@PathVariable("approverId") Integer approverId) {
        ApproverDto approverDto = approverService.findApproverByRecId(approverId);
        if (approverDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(approverDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMA')")
    @PostMapping(value = "/approver/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createApprover(@RequestBody @Valid ApproverRequest approverRequest) {
        ApproverDto approverDto = approverService.createApprover(approverRequest);
        if (approverDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(approverDto, new ApiResponseStatus()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMA')")
    @PostMapping(value = "/approver/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchApprover(@RequestBody @Valid ApproverSearchRequest approverSearchRequest) {
        Sort sort;
        int page = approverSearchRequest.getPage();
        int size = approverSearchRequest.getPageSize();
        String sortBy = approverSearchRequest.getSortBy();
        String sortOrder = approverSearchRequest.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(SearchApproverType.APPROVER_NAME.description()).descending();
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
        ApproverSearchDto approverSearchDto = approverService.searchApproverListByCondition(approverSearchRequest, pageable);
        ApproverSearchResponse response = ApproverSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(approverSearchDto.getTotal())
                .totalPage(approverSearchDto.getTotalPage())
                .data(approverSearchDto.getApproverDtoList())
                .build();

        if (approverSearchDto.getApproverDtoList() != null && !approverSearchDto.getApproverDtoList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMA')")
    @PutMapping(value = "/approver/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateApprover(@RequestBody ApproverRequest approverRequest) {
        ApproverDto approverDto = approverService.updateApprover(approverRequest);
        if (approverDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(approverDto, new ApiResponseStatus()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMA')")
    @PutMapping(value = "/approver/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateApproverSequence(@RequestBody SequenceRequest request) {
        ApproverDto approverDto = approverService.updateApproverSequence(request);
        if (approverDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(approverDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SMA')")
    @DeleteMapping(value = "/approver/{approverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteReportLine(@PathVariable("approverId") Integer approverId) {
        boolean isDeleted = approverService.deleteApprover(approverId);
        if (isDeleted) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
