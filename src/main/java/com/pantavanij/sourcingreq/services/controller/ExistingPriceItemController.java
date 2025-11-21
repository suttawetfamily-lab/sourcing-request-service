package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.DeleteExistingPriceItemRequest;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ExistingPriceItemService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SourcingStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
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
import java.math.BigDecimal;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_QUALIFIED_SUPPLIER;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class ExistingPriceItemController {
    private final ExistingPriceItemService existingPriceItemService;
    private final TenantService tenantService;
    private final RequestService requestService;
    private final SourcingStatusService sourcingStatusService;

    @PostMapping(value = "/existing-price-item/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity submitExistingPriceItem(@Valid @RequestBody ExistingPriceItemRequest existingPriceItemRequest){
        if(existingPriceItemRequest.getUnitPrice().equals(BigDecimal.ZERO)) {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7057, ApiMessage.E7057.description()), HttpStatus.OK);
        }
        // Get supplier web work
        return checkExistingPriceItemResponse(existingPriceItemRequest, true);
    }

    @PostMapping(value = "/existing-price-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveExistingPriceItem(@Valid @RequestBody ExistingPriceItemRequest existingPriceItemRequest){
       return checkExistingPriceItemResponse(existingPriceItemRequest, false);
    }

    @DeleteMapping(value = "/existing-price-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiErrorResponse> deleteRequestItem(@Valid @RequestBody DeleteExistingPriceItemRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        boolean isReject = existingPriceItemService.rejectExistingPriceItem(tenant.getRecId(), request);
        if(isReject) {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.I1008, ApiMessage.I1008.description()), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7063, ApiMessage.E7063.description()) , HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = {"/existing-price-item/{pathUrl}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExistingPriceItemListResponse> getExistingPriceItemList(@RequestParam(value = "requestId") final Long requestId
            , @RequestParam(value = "authCode") String authCode
            , @RequestParam(value = "page") int page
            , @RequestParam(value = "size") int size
            , @Valid @PathVariable String pathUrl) {

        Sort sort = Sort.by("ItemSequence").ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Request requestOptional = requestService.searchRequestByRecId(requestId);
        ExistingPriceItemSearchDto existingPriceItemSearchDto = existingPriceItemService.getItemByRequest(requestOptional, pageable, authCode, pathUrl);
        if(existingPriceItemSearchDto.getExistingPriceItemDtoList() != null &&
                existingPriceItemSearchDto.getExistingPriceItemDtoList().size() > 0) {

            return ResponseEntity.status(HttpStatus.OK).body(new ExistingPriceItemListResponse(
                    existingPriceItemSearchDto.getExistingPriceItemDtoList(),
                    existingPriceItemSearchDto.getPage(),
                    existingPriceItemSearchDto.getPageSize(),
                    existingPriceItemSearchDto.getTotal(),
                    existingPriceItemSearchDto.getTotalPage()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExistingPriceItemListResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = {"/existing-price-item/{pathUrl}/v2"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExistingPriceItemListResponseV2> getExistingPriceItemListV2(
            @RequestParam(value = "requestId") final Long requestId,
            @RequestParam(value = "authCode") String authCode,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sourcingTypeId", required = false) Integer sourcingTypeId,
            @RequestParam(value = "sourcingDocNo", required = false) String sourcingDocNo,
            @Valid @PathVariable String pathUrl) {

        Sort sort = Sort.by("ItemSequence").ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Request requestOptional = requestService.searchRequestByRecId(requestId);
        ExistingPriceItemSearchDtoV2 existingPriceItemSearchDto = existingPriceItemService.getItemByRequestV2(requestOptional, pageable, authCode, pathUrl, sourcingTypeId, sourcingDocNo);
        if(existingPriceItemSearchDto.getExistingPriceItemDtoList() != null &&
                existingPriceItemSearchDto.getExistingPriceItemDtoList().size() > 0) {

            return ResponseEntity.status(HttpStatus.OK).body(new ExistingPriceItemListResponseV2(
                    existingPriceItemSearchDto.getExistingPriceItemDtoList(),
                    existingPriceItemSearchDto.getPage(),
                    existingPriceItemSearchDto.getPageSize(),
                    existingPriceItemSearchDto.getTotal(),
                    existingPriceItemSearchDto.getTotalPage()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExistingPriceItemListResponseV2(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = {"/existing-price-item-id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExistingPriceItemIdListResponse> getExistingPriceItemIDList(
            @RequestParam(value = "requestId") final Long requestId) {

        Request requestOptional = requestService.searchRequestByRecId(requestId);
        List<Long> requestItemIdList = existingPriceItemService.getAllItemByRequest(requestOptional);
        if(requestItemIdList != null && requestItemIdList.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new ExistingPriceItemIdListResponse(
                    requestItemIdList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExistingPriceItemIdListResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = {"/existing-price-item-all/{pathUrl}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExistingPriceItemAllResponse> getExistingPriceItemAll(
            @RequestParam(value = "requestId") final Long requestId,
            @RequestParam(value = "requestItemIdList") final List<Long> requestItemIdList,
            @Valid @PathVariable String pathUrl) {

        Request requestOptional = requestService.searchRequestByRecId(requestId);
        List<ExistingPriceItemDtoV2> existingPriceItemList = existingPriceItemService.getAllItemByIdList(requestOptional, requestItemIdList, pathUrl);
        if(existingPriceItemList != null && existingPriceItemList.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new ExistingPriceItemAllResponse(
                    existingPriceItemList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExistingPriceItemAllResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = {"/existing-price-item/copy-to-pr"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getExistingPriceItemListForCopyToPR(@RequestParam(value = "requestId") final Long requestId
            , @RequestParam(value = "authCode") String authCode
            , @RequestParam(value = "page") int page
            , @RequestParam(value = "size") int size) {

        Request request = requestService.searchRequestByRecId(requestId);

        if (request != null) {
            SourcingStatus sourcingStatus = sourcingStatusService.getSourcingStatusById(SOURCING_QUALIFIED_SUPPLIER.id());
            Sort sort = Sort.by("recId").ascending();
            Pageable pageable = PageRequest.of(page - 1, size, sort);

            ExistingPriceItemSearchDto existingPriceItemSearchDto = existingPriceItemService.getItemByRequestAndSourcingStatus(request, sourcingStatus, pageable, authCode);
            ExistingPriceItemListResponse existingPriceItemListResponse = ExistingPriceItemListResponse.builder()
                    .data(existingPriceItemSearchDto.getExistingPriceItemDtoList())
                    .page(page)
                    .pageSize(size)
                    .total(existingPriceItemSearchDto.getTotal())
                    .totalPage(existingPriceItemSearchDto.getTotalPage())
                    .build();
            return ResponseEntity.ok().body(existingPriceItemListResponse);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @GetMapping(value = {"/existing-price-item/status/{pathUrl}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity existingPriceItemStatusUpdate(
            @RequestParam(value = "requestId") final Long requestId,
            @RequestParam(value = "authCode") String authCode,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sourcingTypeId", required = false) Integer sourcingTypeId,
            @RequestParam(value = "sourcingDocNo", required = false) String sourcingDocNo,
            @Valid @PathVariable String pathUrl) {

        Sort sort = Sort.by("recId").ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Request requestOptional = requestService.searchRequestByRecId(requestId);

        SourcingItemSearchDto sourcingItemSearchDto =
                existingPriceItemService.updateItemStatusByRequest(requestOptional, pageable, authCode, pathUrl, sourcingTypeId, sourcingDocNo);

        if (sourcingItemSearchDto.getSourcingItemDtoList() != null
                && !sourcingItemSearchDto.getSourcingItemDtoList().isEmpty()) {

            SourcingItemListResponse sourcingItemListResponse = SourcingItemListResponse.builder()
                    .approvalStatus(sourcingItemSearchDto.getApprovalStatus())
                    .requestStatus(sourcingItemSearchDto.getRequestStatus())
                    .sourcingItemList(sourcingItemSearchDto.getSourcingItemDtoList())
                    .approverHeaders(sourcingItemSearchDto.getApproverHeaders())
                    .page(page)
                    .pageSize(size)
                    .total(sourcingItemSearchDto.getTotal())
                    .totalPage(sourcingItemSearchDto.getTotalPage())
                    .build();
            return ResponseEntity.ok().body(sourcingItemListResponse);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }


    @PreAuthorize("hasAnyAuthority('SQA', 'SQP')")
    @GetMapping(value = "/tenant-config/existing-price-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SourcingRequestVisibleConfigResponse> hideExistingPriceOption() {
        SourcingRequestVisibleConfig sourcingRequestVisibleConfig = existingPriceItemService.getExistingPriceOptionConfig();
        return new ResponseEntity<>(new SourcingRequestVisibleConfigResponse(sourcingRequestVisibleConfig), HttpStatus.OK);
    }

    private ResponseEntity checkExistingPriceItemResponse(ExistingPriceItemRequest existingPriceItemRequest, boolean isSubmit){
        Long existingPriceItemId = existingPriceItemService.saveExistingPriceItem(existingPriceItemRequest, isSubmit);
        if (existingPriceItemId != 0L) {
            return new ResponseEntity<>(new ExistingPriceItemResponse(existingPriceItemId), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }
}
