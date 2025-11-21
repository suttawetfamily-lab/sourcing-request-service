package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;

import java.math.BigDecimal;

public interface ExistingPriceItemSupplierService {
    void saveOrUpdateERFX(
            String basePrice,
            BigDecimal unitPrice,
            String awardedType,
            BigDecimal awardedValue,
            Integer supplierId,
            String taxId,
            ExistingPriceItem existingPriceItem
    );
    void saveOrUpdateExistingPrice(ExistingPriceItemRequest existingPriceItemRequest, Request request, RequestItem requestItem, Supplier supplier, ExistingPriceItem existingPriceItem);
}
