package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.ExistingPriceItemSupplierKey;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ExistingPriceItemSupplierRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SupplierRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ExistingPriceItemSupplierService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ExistingPriceItemSupplierServiceImpl implements ExistingPriceItemSupplierService {

    private final ExistingPriceItemSupplierRepository existingPriceItemSupplierRepository;

    private final SupplierRepository supplierRepository;

    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateERFX(
            String basePrice,
            BigDecimal unitPrice,
            String awardedType,
            BigDecimal awardedValue,
            Integer supplierId,
            String taxId,
            ExistingPriceItem existingPriceItem
    ) {
        List<ExistingPriceItemSupplier> existingPriceItemSupplierList = new ArrayList<>();
        Optional<ExistingPriceItemSupplier> existingExistingPriceItemSupplier = null;

        if (existingPriceItem.getExistingPriceItemSupplierList() != null && !existingPriceItem.getExistingPriceItemSupplierList().isEmpty()) {
            existingExistingPriceItemSupplier = existingPriceItem.getExistingPriceItemSupplierList().stream().filter(i -> i.getSupplier().getRecId() == supplierId).findFirst();
        }

        if (supplierId == null ) return;

        if (existingExistingPriceItemSupplier != null && existingExistingPriceItemSupplier.isPresent()) {
            ExistingPriceItemSupplier existingPriceItemSupplier = existingExistingPriceItemSupplier.get();
            if (CommonUtils.isNumeric(basePrice)) {
                BigDecimal bdBasePrice = new BigDecimal(basePrice);
                existingPriceItemSupplier.setBasePrice(bdBasePrice);
            }
            existingPriceItemSupplier.setUnitPrice(unitPrice);
            existingPriceItemSupplier.setAwardedType(awardedType);
            existingPriceItemSupplier.setAwardedValue(awardedValue.toString());
            existingPriceItemSupplier.setTaxId(taxId);
            existingPriceItemSupplier.setUpdatedBy(AppUtil.getUserName());
            existingPriceItemSupplier.setUpdatedDate(DateTimeUtil.getTimestampUTC());

            existingPriceItemSupplierList.add(existingPriceItemSupplier);
        } else {

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            boolean isShowSupplierLocalLanguage = tenantConfigService.isShowSupplierLocalLanguage(tenant.getRecId());
            String supplierFieldName = tenantConfigService.getSupplierFieldName(tenant.getRecId());
            Optional<Supplier> supplier = supplierRepository.findSupplierByRecId(supplierId);

            BigDecimal bdBasePrice = new BigDecimal(0);
            if (CommonUtils.isNumeric(basePrice)) {
                bdBasePrice = new BigDecimal(basePrice);
            }

            if(supplier.isPresent()) {
                ExistingPriceItemSupplier existingPriceItemSupplier = ExistingPriceItemSupplier.builder()
                        .id(new ExistingPriceItemSupplierKey(existingPriceItem.getRecId(), supplierId))
                        .supplier(supplier.get())
                        .existingPriceItem(existingPriceItem)
                        .supplierShortName(supplier.get().getShortName())
                        .supplierFullName(SupplierNameUtil.resolveSupplierName(supplier.get(), isShowSupplierLocalLanguage, supplierFieldName))
                        .taxId(supplier.get().getTaxId())
                        .awardedType(awardedType)
                        .awardedValue(awardedValue != null ? awardedValue.toString() : null)
                        .basePrice(bdBasePrice)
                        .unitPrice(unitPrice)
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .build();

                existingPriceItemSupplierList.add(existingPriceItemSupplier);
            }
        }

        existingPriceItemSupplierRepository.saveAll(existingPriceItemSupplierList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateExistingPrice(
            ExistingPriceItemRequest existingPriceItemRequest,
            Request request,
            RequestItem requestItem,
            Supplier supplier,
            ExistingPriceItem existingPriceItem) {

        if (existingPriceItem.getExistingPriceItemSupplierList() != null
                && !existingPriceItem.getExistingPriceItemSupplierList().isEmpty()) {
            existingPriceItemSupplierRepository.deleteExistingPriceItemSupplierByExistingPriceItemId(existingPriceItem.getRecId());
        }

        if (supplier == null) return;

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        boolean isShowSupplierLocalLanguage = tenantConfigService.isShowSupplierLocalLanguage(tenant.getRecId());
        String supplierFieldName = tenantConfigService.getSupplierFieldName(tenant.getRecId());

        String awardedType = Constant.NA;
        String awardedValue = request.getRequestTypeId() == 1
                ? existingPriceItemRequest.getAwardedQuantity()
                : existingPriceItemRequest.getAwardedAmount();

        existingPriceItemSupplierRepository.saveExistingPriceItemSupplier(
                existingPriceItem.getRecId(),
                supplier.getRecId(),
                supplier.getShortName(),
                SupplierNameUtil.resolveSupplierName(supplier, isShowSupplierLocalLanguage, supplierFieldName),
                supplier.getTaxId(),
                awardedType,
                awardedValue,
                existingPriceItemRequest.getUnitPrice(),
                AppUtil.getUserName(),
                DateTimeUtil.getTimestampUTC()
        );
    }

}
