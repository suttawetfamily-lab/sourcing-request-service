package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Pr;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.PurchaseRequisitionResponse;
import com.pantavanij.sourcingreq.services.domain.response.pr.CreateOraclePrResponse;

import java.util.List;

public interface PrService {
    PurchaseRequisitionResponse createPurchaseRequisition(String requestNo, List<Long> requestItemsId);
    CreateOraclePrResponse createERPPurchaseRequisition(Tenant tenant, String requestNo, List<Long> requestItemsId);
    Pr savePrNumber(String prNumber);
    void saveRequestItemPR(Integer prId, List<Long> requestItemsId);
}
