package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purchaser;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthPurchaserResponse;
import org.springframework.data.domain.*;

import java.util.*;

public interface PurchaserService {

    List<OptionDetailDto> getPurchaserByTenantIdAndSearchTermAndCategoryId(Integer tenantId, String searchTerm, Integer categoryId);

    List<Purchaser> getByTenantId(Integer tenantId);

    PurchaserDto createPurchaser(PurchaserRequest purchaserRequest);

    PurchaserSearchDto searchPurchaserByCondition(PurchaserSearchRequest purchaserSearchRequest, Pageable pageable);

    PurchaserDto findPurchaserByRecId(Integer purchaserId);

    PurchaserDto updatePurchaser(PurchaserRequest purchaserRequest);

    boolean deletePurchaserByRecId(Integer purchaserId);
    List<EPAuthUserDTO> getAllPurchaser(EPAuthUserSearchRequest epaAuthUserSearchRequest);

    PurchaserDto updatePurchaserSequence(SequenceRequest request);

    EPAuthPurchaserResponse getPurchaserListByConditions(PurchaserSearchRequest request);
}
