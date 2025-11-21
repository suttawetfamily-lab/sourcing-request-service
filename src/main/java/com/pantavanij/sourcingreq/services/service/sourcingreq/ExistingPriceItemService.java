package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.DeleteExistingPriceItemRequest;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemRequest;
import com.pantavanij.sourcingreq.services.domain.response.UserDetailResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ExistingPriceItemService {
    Long saveExistingPriceItem(ExistingPriceItemRequest existingPriceItemRequest, boolean isSubmit);

    boolean deleteExistingPriceItem(Integer tenantId, DeleteExistingPriceItemRequest request);

    ExistingPriceItem getExistingPriceItemByCondition(Long requestId, Long requestItemId, Integer tenantId, String sourcingDocNo);

    ExistingPriceItem updateExistingPriceItem(
            ShortlistDto shortlist,
            String unitCode,
            Long requestId,
            Long requestItemId,
            Integer tenantId,
            String awardedType,
            String sourcingDocNo
    );

    ExistingPriceItemSearchDto getItemByRequestAndSourcingStatus(Request request, SourcingStatus sourcingStatus, Pageable pageable, String authCode);

    ExistingPriceItemSearchDto getItemByRequest(Request request, Pageable pageable, String authCode, String pathUrl);

    ExistingPriceItemSearchDtoV2 getItemByRequestV2(Request request, Pageable pageable, String authCode, String pathUrl, Integer sourcingTypeId, String sourcingDocNo);

    List<Long> getAllItemByRequest(Request request);

    List<ExistingPriceItemDtoV2> getAllItemByIdList(Request request, List<Long> requestItems, String pathUrl);

    ExistingPriceItemResponseDto getExistingPriceItemByRequestIdAndRequestItemId(Long requestId, Long requestItemId, Integer tenantId, String sourcingDocNo);

    SourcingItemSearchDto updateItemStatusByRequest(Request request, Pageable pageable, String authCode, String pathUrl, Integer sourcingTypeId, String sourcingDocNo);

    void getERFXSourcingStatus(List<ERFXStatusDto> eRFXStatusDtos, RequestItem requestItem);

    SourcingItemDto sourcingItemDetail(RequestItem requestItem, String authCode, String timeZone, String pathUrl, Map<String, UserDetailResponse> userDetailMap, DelegationDto delegationDto, ExcSourcing excSourcing);

    SourcingRequestVisibleConfig getExistingPriceOptionConfig();

    boolean rejectExistingPriceItem(Integer tenantId, DeleteExistingPriceItemRequest request);

}
