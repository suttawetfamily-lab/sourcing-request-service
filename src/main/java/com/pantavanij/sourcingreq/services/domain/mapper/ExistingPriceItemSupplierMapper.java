package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemSupplierDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_QUANTITY;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.*;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_ERFX;


@Mapper
public interface ExistingPriceItemSupplierMapper {

    ExistingPriceItemSupplierMapper INSTANCE = Mappers.getMapper(ExistingPriceItemSupplierMapper.class);

    List<ExistingPriceItemSupplierDto> toExistingPriceItemSupplierDtoList(List<ExistingPriceItemSupplier> existingPriceItemSuppliers);

    ExistingPriceItemSupplierDto toExistingPriceItemSupplierDto(ExistingPriceItemSupplier existingPriceItemSupplier);

    default ExistingPriceItemSupplierDto toExistingPriceItemSupplierDto(int requestType, RequestItem requestItem, ExistingPriceItem existingPriceItem, ExistingPriceItemSupplier existingPriceItemSupplier, String timeZone) {
        ExistingPriceItemSupplierDto existingPriceItemSupplierDto = toExistingPriceItemSupplierDto(existingPriceItemSupplier);
        if (existingPriceItemSupplierDto != null) {
            existingPriceItemSupplierDto.setSupplierObj(OptionDtoMapper.INSTANCE.toSupplierOptionDto(existingPriceItemSupplier));
            if (existingPriceItemSupplier != null
                    && (existingPriceItemSupplier.getUnitPrice() != null && existingPriceItemSupplier.getUnitPrice().doubleValue() == 0)
                    && requestItem.getSourcingStatus().getRecId() != SOURCING_AWAITING_SHORTLIST.id()
                    && requestItem.getSourcingType().getRecId() == SOURCING_TYPE_ERFX.id()) {
                existingPriceItemSupplierDto.setFreeItem(true);
            } else {
                existingPriceItemSupplierDto.setFreeItem(false);
            }

            if (existingPriceItemSupplier != null &&
                    (existingPriceItemSupplier.getAwardedValue() != null && existingPriceItemSupplier.getUnitPrice() != null)
            ) {
                String awardedValueStr = null;
                BigDecimal awardedValueNumber = null;

                try {
                    awardedValueNumber = new BigDecimal(existingPriceItemSupplier.getAwardedValue());
                } catch (NumberFormatException e) {
                    awardedValueStr = existingPriceItemSupplier.getAwardedValue();
                }

                BigDecimal totalAwardedNetAmount = null;
                if (awardedValueNumber != null) {
                    totalAwardedNetAmount = existingPriceItemSupplier.getUnitPrice().multiply(awardedValueNumber);
                }

                String awardedValue = awardedValueNumber != null ? awardedValueNumber.toString() : awardedValueStr;
                if (requestType == 1 && (existingPriceItemSupplier.getAwardedType().equalsIgnoreCase(Constant.QTY) ||
                        existingPriceItemSupplier.getAwardedType().equalsIgnoreCase(Constant.NA))
                ) {
                    existingPriceItemSupplierDto.setAwardedNetAmount(totalAwardedNetAmount);
                    existingPriceItemSupplierDto.setAwardedValue(awardedValue);

                } else if (requestType == 2) {
                    switch (existingPriceItemSupplier.getAwardedType()) {
                        case Constant.QTY:
                            existingPriceItemSupplierDto.setAwardedNetAmount(totalAwardedNetAmount);
                            existingPriceItemSupplierDto.setAwardedValue(awardedValue);
                            break;
                        case Constant.AMT:
                            existingPriceItemSupplierDto.setAwardedNetAmount(awardedValueNumber);
                            existingPriceItemSupplierDto.setAwardedValue(awardedValue);
                            break;
                        case Constant.NA:
                            existingPriceItemSupplierDto.setAwardedNetAmount(existingPriceItem.getUnitPrice());
                            existingPriceItemSupplierDto.setAwardedValue(awardedValue);
                            break;
                        default:
                            existingPriceItemSupplierDto.setAwardedNetAmount(null);
                            existingPriceItemSupplierDto.setAwardedValue(null);
                            break;
                    }
                }
            } else {
                if(requestItem.getRequest().getRequestTypeId() == REQUEST_TYPE_QUANTITY.id() &&
                        requestItem.getSourcingStatus().getRecId() == SOURCING_QUALIFIED_SUPPLIER.id() &&
                        existingPriceItemSupplier.getUnitPrice() != null
                ) {
                    existingPriceItemSupplierDto.setAwardedNetAmount(existingPriceItemSupplier.getUnitPrice().multiply(existingPriceItem.getQuantity()));
                    existingPriceItemSupplierDto.setAwardedValue(existingPriceItem.getQuantity().toString());
                } else {
                    existingPriceItemSupplierDto.setAwardedNetAmount(null);
                    existingPriceItemSupplierDto.setAwardedValue(null);
                }
            }

            existingPriceItemSupplierDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(existingPriceItemSupplierDto.getCreatedDate(), timeZone));
            existingPriceItemSupplierDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(existingPriceItemSupplierDto.getUpdatedDate(), timeZone));
        }
        return existingPriceItemSupplierDto;
    }

    default List<ExistingPriceItemSupplierDto> toExistingPriceItemSupplierDtoList(int requestType, RequestItem requestItem, ExistingPriceItem existingPriceItem, List<ExistingPriceItemSupplier> existingPriceItemSupplierList, String timeZone) {
        List<ExistingPriceItemSupplierDto> existingPriceItemSupplierDtoList = new ArrayList<>();
        if (existingPriceItemSupplierList != null) {
            existingPriceItemSupplierList.forEach(i -> existingPriceItemSupplierDtoList.add(toExistingPriceItemSupplierDto(requestType, requestItem, existingPriceItem, i, timeZone)));
        }
        return existingPriceItemSupplierDtoList;
    }

}
