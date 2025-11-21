package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_UNKNOWN;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_CONDITION;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_QUANTITY;
import static com.pantavanij.sourcingreq.services.enums.SectionType.SECTION_TYPE_REQI;
import static com.pantavanij.sourcingreq.services.enums.ValidatorType.VALIDATOR_REQUIRED;

@Slf4j
@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class RequestItemController {
    private final RequestItemService requestItemService;
    private final RequestService requestService;
    private final TenantService tenantService;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final ExcelService excelService;
    private final TenantSectionDetailService tenantSectionDetailService;
    private final FileUtil fileUtil;
    private final RequestReviewerRepository requestReviewerRepository;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final DelegationService delegationService;
    private final RequestReportLineRepository requestReportLineRepository;
    private final RequestApproverRepository requestApproverRepository;

    @PostMapping(value = "/request-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addRequestItem(@Valid @RequestBody RequestItemRequest requestItemRequest, @RequestParam(value = "isSubmit") boolean isSubmit) {
        Request request = requestService.searchRequestByRecId(requestItemRequest.getRequestId());
        if (isSubmit) {
            if (request.getRequestTypeId().equals(REQUEST_TYPE_QUANTITY.id())) {
                if (requestItemRequest.getQuantity().equals(BigDecimal.ZERO)) {
                    return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7057, ApiMessage.E7057.description()), HttpStatus.OK);
                }
            } else if (request.getRequestTypeId().equals(REQUEST_TYPE_CONDITION.id())) {
                String tenantCode = AppUtil.getTenantId();
                Tenant tenant = tenantService.findByCode(tenantCode);
                if(tenantSectionDetailService.isRequiredField(tenant.getRecId(), "conditions", VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code())) {
                    if (requestItemRequest.getConditions() == null || requestItemRequest.getConditions().isEmpty()) {
                        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7061, ApiMessage.E7061.description()), HttpStatus.OK);
                    }
                }
            }
        }

        RequestItemDto requestItemDto = requestItemService.saveRequestItem(requestItemRequest);
        if (requestItemDto != null) {
            return new ResponseEntity<>(new RequestItemResponse(requestItemDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @GetMapping(value = "/request-item/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRequestItemList(@RequestParam(value = "requestId") final Long requestId
            , @RequestParam(value = "page") int page
            , @RequestParam(value = "size") int size) {

        UserDto user = AppUtil.getUser();
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();

        Sort sort = Sort.by("recId").ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Request requestOptional = requestService.searchRequestByRecId(requestId);
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(requestOptional);
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(requestOptional.getRecId());
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(requestOptional.getRecId());
        List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(requestOptional.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(requestOptional.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        if (AppUtil.isAllowedAction(user, requestOptional, requestPurchasers, requestApprovers, null, null, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_UNKNOWN)) {
            RequestItemSearchDto requestPage = requestItemService.findByRequest(requestOptional, pageable);
            if (requestPage.getRequestItemDtoList() != null && requestPage.getRequestItemDtoList().size() > 0) {
                RequestItemListResponse requestItemListResponse = RequestItemListResponse.builder()
                        .data(requestPage.getRequestItemDtoList())
                        .page(page)
                        .pageSize(size)
                        .total(requestPage.getTotalPage())
                        .build();
                return ResponseEntity.ok().body(requestItemListResponse);
            } else {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @GetMapping(value = "/request-item/{requestItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRequestItemDetail(@PathVariable Long requestItemId) {
        RequestItemV2Dto requestItem = requestItemService.findRequestItemByRecId(requestItemId);
        if (requestItem != null) {
            return ResponseEntity.ok().body(requestItem);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/request-item/{requestItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteRequestItem(@PathVariable Long requestItemId) {
        requestItemService.deleteRequestItemByRecId(requestItemId);
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.I1004, ApiMessage.I1004.description()), HttpStatus.OK);
    }

    @DeleteMapping(value = "/request-item/sourcing", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiErrorResponse> rejectRequestItem(@Valid @RequestBody DeleteRequestItemRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        boolean isReject = requestItemService.rejectSourcingRequestItem(tenant.getRecId(), request);
        if (isReject) {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.I1008, ApiMessage.I1008.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7063, ApiMessage.E7063.description()), HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @PostMapping(value = "/request-item/sourcing-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateSourcingStatus(@Valid @RequestBody RequestItemSourcingStatusRequest request) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        requestItemService.updateRequestItemBySourcingStatus(request.getSourcingStatusId(), request.getSourcingDocNo(), tenant.getRecId());
        return new ResponseEntity<>(new ApiResponse(null), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SQW')")
    @PostMapping(value = "/request-item/export/{pathURL}/{requestItemReportId}", produces = "application/vnd.ms-excel")
    public ResponseEntity exportRequest(
            @RequestBody RequestSearchRequest searchRequest,
            @PathVariable String pathURL,
            @PathVariable Integer requestItemReportId,
            @RequestParam(value = "organizationId", required = false) Integer organizationId
    ) {
        try {
            if (searchRequest.getConditionSearchList().stream().noneMatch(s -> s.getSearchValue().length() > 0
                    && s.getSearchValue().length() < 1)) {

                // Export Excel results
                List<ExcelRequestItemDto> excelData = requestItemService.searchRequestItemExcelByCondition(searchRequest);
                String fileName = excelService.generateRequestItemReport(excelData, pathURL, requestItemReportId, organizationId);

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

        } catch (Exception e) {
            log.error("Export Request File Exception : " + e.getMessage(), e);
            MediaType mediaType = MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE);
            ApiErrorResponse status = new ApiErrorResponse(ApiMessage.E1001, ApiMessage.E1001.description());
            return ResponseEntity.internalServerError()
                    .contentType(mediaType)
                    .body(status);
        }
    }


    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @GetMapping(value = "/request-item/purchaser/export", produces = "application/vnd.ms-excel")
    public ResponseEntity exportAllPurchaser() {
        try {
            String templateFileName = "Category_Sub-Category_Purchaser.xlsx";
            MediaType mediaType = MediaType.parseMediaType("application/vnd.ms-excel");
            String tenantCode = AppUtil.getTenantId();
            Tenant tenant = tenantService.findByCode(tenantCode);

            String customFolderName = String.format("attachments/%s/template", tenant.getCode());
            ByteArrayResource resource = new ByteArrayResource(fileUtil.downloadFile(templateFileName, customFolderName));


            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + templateFileName)
                    .contentType(mediaType)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("Export Purchaser Template File Exception : " + e.getMessage(), e);
            MediaType mediaType = MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE);
            ApiErrorResponse status = new ApiErrorResponse(ApiMessage.E1001, ApiMessage.E1001.description());
            return ResponseEntity.internalServerError()
                    .contentType(mediaType)
                    .body(status);
        }
    }

    @PostMapping(value = "/request-item/{requestTypeId}/{typeId}/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UploadRequestItemResponse>> uploadRequestItem(@PathVariable Integer requestTypeId, @PathVariable Integer typeId, @RequestPart("file") MultipartFile file, @RequestParam("organizationId") Integer organizationId) throws IOException, NoSuchFieldException, IllegalAccessException, InvalidFormatException, ParseException {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        UploadRequestItemResponse uploadRequestItemResponse = requestItemService.validateAndReadFileExcel(requestTypeId, typeId, file, tenant, organizationId);
        if (uploadRequestItemResponse.isValid()) {
           return new ResponseEntity<>(new ApiResponse(uploadRequestItemResponse), HttpStatus.OK);
        } else {
           return ResponseEntity.status(HttpStatus.OK).body(
                   new ApiResponse(uploadRequestItemResponse, new ApiResponseStatus(ApiMessage.E7053, ApiMessage.E7053.description())));
        }
    }

    @GetMapping(value = "/request-item/{requestTypeId}/{typeId}/template", produces = "application/vnd.ms-excel")
    public ResponseEntity downloadRequestItemTemplate(
            @PathVariable Integer requestTypeId,
            @PathVariable Integer typeId,
            @RequestHeader(value = "organization", required = false) Integer organizationId
    ) {
        try {
            String fileName = excelService.generateRequestItemTemplate(requestTypeId, typeId, organizationId); // type = non-it, it
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
        } catch (Exception e) {
            log.error("DownloadTemplate RequestItem Exception : {}", e.getMessage());
            MediaType mediaType = MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE);
            ApiErrorResponse status = new ApiErrorResponse(ApiMessage.E1001, ApiMessage.E1001.description());
            return ResponseEntity.internalServerError()
                    .contentType(mediaType)
                    .body(status);
        }
    }

}
