package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.DeleteRequestItemRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.UploadRequestItemResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface RequestItemService {
    RequestItemDto saveRequestItem(RequestItemRequest requestItemRequest);

    List<RequestItemDto> getRequestItemByRequestId(Long requestId, boolean isCopiedToPR);

    List<RequestItemV2Dto> getRequestItemByRequestId(Long requestId);

    RequestItemSearchDto findByRequest(Request request, Pageable pageable);

    RequestItemV2Dto findRequestItemByRecId(Long requestItemId);

    void deleteRequestItemByRecId(Long requestItemId);

    boolean deleteSourcingRequestItem(Integer tenantId, DeleteRequestItemRequest request);

    RequestItem getRequestItemsByCondition(Long itemId, String sourcingDocNo, String sourcingDocId);

    RequestItem updateRequestItem(ShortlistDto shortlist, SourcingStatus sourcingStatus, String sourcingDocNo);

    void updateRequestItemBySourcingStatus(Integer sourcingStatusId, String sourcingDocNo, Integer tenantId);

    List<Long> getSourcingRequestItemByRequestId(Long requestId);

    List<RequestItemDto> getRequestItemByRequestIdAndSourcingStatusId(Long requestId, Integer sourcingStatusId);

    List<RequestItemV2Dto> getRequestItemByRequestIdList(Long requestId, List<Long> requestItemsId, Integer tenantId);

    RequestItem getRequestItemByRequestItemId(Long requestItemId);

    UploadRequestItemResponse validateAndReadFileExcel(Integer requestTypeId, Integer typeId, MultipartFile file, Tenant tenant, Integer organizationId) throws IOException, NoSuchFieldException, IllegalAccessException, InvalidFormatException, ParseException;

    boolean rejectSourcingRequestItem(Integer tenantId, DeleteRequestItemRequest request);

    List<ExcelRequestItemDto> searchRequestItemExcelByCondition(RequestSearchRequest searchRequest);
}
