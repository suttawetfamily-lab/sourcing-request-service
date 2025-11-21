package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemAdditional;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemAdditionalRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemAdditionalService;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.unbescape.html.*;

import java.math.*;
import java.util.*;

@RequiredArgsConstructor
@Service
public class RequestItemAdditionalServiceImpl implements RequestItemAdditionalService {

    private final RequestItemAdditionalRepository requestItemAdditionalRepository;

    @Override
    public RequestItemAdditional saveOrUpdate(RequestItem requestItem, ShortlistDto shortlist, String timeZone) {
        Optional<RequestItemAdditional> requestItemAdditionalOptional = requestItemAdditionalRepository.findByRequestItemId(requestItem.getRecId());
        RequestItemAdditional requestItemAdditional = null;

        if (requestItemAdditionalOptional.isPresent()) {
            requestItemAdditional = requestItemAdditionalOptional.get();
            requestItemAdditional.setUpdatedBy(AppUtil.getUserName());
            requestItemAdditional.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        } else {
            requestItemAdditional = new RequestItemAdditional();
            requestItemAdditional.setCreatedBy(AppUtil.getUserName());
            requestItemAdditional.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }

        requestItemAdditional.setRequestItem(requestItem);
        requestItemAdditional.setBidStartDate(shortlist.getBidStartDate());
        requestItemAdditional.setBidCompleteDate(shortlist.getBidCompleteDate());
        requestItemAdditional.setBidCompleteMonth(DateTimeUtil.getMonth(shortlist.getBidCompleteDate(), timeZone));
        requestItemAdditional.setBidCompleteYear(DateTimeUtil.getYear(shortlist.getBidCompleteDate(), timeZone));
        requestItemAdditional.setBidNo(shortlist.getBidNo());
        requestItemAdditional.setBiddingType(shortlist.getBiddingType());
        requestItemAdditional.setBidDescription(shortlist.getBidDescription());
        requestItemAdditional.setVatType(shortlist.getVatType());

//        if (CommonUtils.isNumeric(shortlist.getBasePrice())) {
//            BigDecimal basePrice = new BigDecimal(shortlist.getBasePrice());
//            requestItemAdditional.setBasePrice(basePrice);
//        }
        requestItemAdditional.setCostAvoidanceVat7Percentage(shortlist.getCostAvoidanceVat7Percentage());

//        calculateValue(requestItemAdditional, shortlist);

        requestItemAdditional = requestItemAdditionalRepository.save(requestItemAdditional);
        return requestItemAdditional;

//        if (!shortlist.getSuppliers().isEmpty()) {
//            for (ErfxSupplierDto supplier : shortlist.getSuppliers()) {
//                if (supplier != null && supplier.getTaxNo() != null) {
//                    requestItemAdditional.setTaxNo(supplier.getTaxNo());
//                }
//                requestItemAdditional = requestItemAdditionalRepository.save(requestItemAdditional);
//                return requestItemAdditional;
//            }
//        }
//        return null;
    }

//    public void calculateValue(RequestItemAdditional requestItemAdditional, ShortlistDto shortlist/*, BigDecimal lastUnitPrice*/) {
//        if (shortlist != null && shortlist.getVatType() != null) {
//            //double qty = shortlist() != null ? shortlist.getQuantity().doubleValue(): BigDecimal.ZERO.doubleValue();
//            final double VAT = 1.07;
//
//            // Excluded vat
//            double totalProjectedPrice = calculateTotalProjectPrice(shortlist, qty);
//            double totalProjectedPriceVat7Percentage = totalProjectedPrice * VAT;
//            requestItemAdditional.setTotalProjectedPrice(new BigDecimal(totalProjectedPrice)
//                    .setScale(4, RoundingMode.HALF_UP));
//
//            // Included vat
//            requestItemAdditional.setTotalProjectedPriceVat7Percentage(new BigDecimal(totalProjectedPriceVat7Percentage)
//                    .setScale(4, RoundingMode.HALF_UP));
//        }
//    }

//    public double calculateTotalProjectPrice(ShortlistDto shortlist, double qty) {
//        if (CommonUtils.isNumeric(shortlist.getBasePrice())) {
//            double basePrice = Double.parseDouble(shortlist.getBasePrice());
//            return qty * basePrice;
//        }
//        return 0.00;
//    }

}
