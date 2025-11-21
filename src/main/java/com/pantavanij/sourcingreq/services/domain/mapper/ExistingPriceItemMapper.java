package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.SupplierNameUtil;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.unbescape.html.HtmlEscape;

import java.math.*;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.*;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_ERFX;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_EXISTING_PRICE;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_EXCEPTIONAL_SOURCING;

@Mapper(uses = {RequestMapper.class, RequestItemMapper.class})
public interface ExistingPriceItemMapper {

    ExistingPriceItemMapper INSTANCE = Mappers.getMapper(ExistingPriceItemMapper.class);

    @Mapping(source = "requestItem", target = "requestItemDto")
    ExistingPriceItemResponseDto toExistingPriceItemResponseDto(ExistingPriceItem existingPriceItem);

    default ExistingPriceItemResponseDto toExistingPriceItemResponseDto(ExistingPriceItem existingPriceItem, String timeZone) {
        ExistingPriceItemResponseDto existingPriceItemResponseDto = toExistingPriceItemResponseDto(existingPriceItem);
        if (existingPriceItemResponseDto != null) {
            existingPriceItemResponseDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(existingPriceItemResponseDto.getCreatedDate(), timeZone));
            existingPriceItemResponseDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(existingPriceItemResponseDto.getUpdatedDate(), timeZone));
            existingPriceItemResponseDto.setRequestItemDto(
                    RequestItemMapper.INSTANCE.toRequestItemV2Dto(existingPriceItem.getRequestItem(), timeZone));
            existingPriceItemResponseDto.setRequest(
                    RequestMapper.INSTANCE.toRequestDto(existingPriceItem.getRequest(), timeZone));
            existingPriceItemResponseDto.setCurrency(CurrencyMapper.INSTANCE.currencyToCurrencyDto(existingPriceItem.getCurrency()));
        }
        return existingPriceItemResponseDto;
    }

    default ExistingPriceItemDto toExistingPriceItemDto(
            RequestItem requestItem,
            ExistingPriceItem existingPriceItem,
            Request request,
            Type type,
            List<RequestItemPr> requestItemPrList,
            List<RequestItemAttachment> requestItemAttachmentList,
            RequestItemLocation requestItemLocation,
            List<ExistingPriceItemAttachment> existingPriceItemAttachmentList,
            ExistingPriceItemSupplier existingPriceItemSupplier,
            SourcingStatusDto sourcingStatusDto,
            String firstName,
            String lastName,
            String timeZone,
            boolean isShowItemNameForFreeItem,
            boolean isShowSupplierLocalLanguage,
            String supplierFieldName
        ) {

        ExistingPriceItemDto existingPriceItemDto = new ExistingPriceItemDto();
        existingPriceItemDto.setRequestId(requestItem.getRequest().getRecId());
        existingPriceItemDto.setRequestTypeId(request.getRequestTypeId());
        existingPriceItemDto.setRequestItemId(requestItem.getRecId());
        existingPriceItemDto.setBudgetTypeName(type != null ?type.getName() : null);
        existingPriceItemDto.setItemName(requestItem.getItemName());
        existingPriceItemDto.setItemDescription(requestItem.getItemDescription());
        existingPriceItemDto.setBrand(requestItem.getBrand());
        existingPriceItemDto.setPartNo(requestItem.getPartNo());
        existingPriceItemDto.setQuantity(requestItem.getQuantity());
        existingPriceItemDto.setCondition(requestItem.getConditions());
        existingPriceItemDto.setDeliveryLocation(requestItem.getDeliveryLocation());
        existingPriceItemDto.setContactName(requestItem.getContactName());
        existingPriceItemDto.setPhone(requestItem.getPhone());
        existingPriceItemDto.setSourcingDocNo(requestItem.getSourcingDocNo());
        existingPriceItemDto.setSourcingDocId(requestItem.getSourcingDocId());
        existingPriceItemDto.setSourcingTypeId(requestItem.getSourcingType().getRecId());
        existingPriceItemDto.setSourcingTypeName(requestItem.getSourcingType().getName());

        if (requestItemLocation != null) {
            existingPriceItemDto.setLocationName(requestItemLocation.getLocation().getName());
        }

        // In case Purchaser view their request, do not allow to edit request item.
        if (AppUtil.isPurchaser()) {
            long sourcingTypeId = requestItem.getSourcingType().getRecId();
            long sourcingStatusId = sourcingStatusDto.getRecId();

            boolean isExceptionalPending =
                    sourcingTypeId == SOURCING_TYPE_EXCEPTIONAL_SOURCING.id()
                            && sourcingStatusId == SOURCING_PENDING.id();

            boolean isNotExistingOrExceptional =
                    sourcingTypeId != SOURCING_TYPE_EXISTING_PRICE.id()
                            && sourcingTypeId != SOURCING_TYPE_EXCEPTIONAL_SOURCING.id();

            boolean isExceptionalQualified =  sourcingTypeId == SOURCING_TYPE_EXCEPTIONAL_SOURCING.id() && sourcingStatusDto.getRecId().equals(SOURCING_QUALIFIED_SUPPLIER.id());

            if (isExceptionalPending || isNotExistingOrExceptional || isExceptionalQualified) {
                sourcingStatusDto.setCanEdit(false);
            }
        }

        existingPriceItemDto.setSourcingStatus(sourcingStatusDto);
        existingPriceItemDto.setUsedToCopiedToPR(!requestItemPrList.isEmpty() && requestItemPrList.size() > 0);
        existingPriceItemDto.setDisplayCopiedToPRIcon(AppUtil.getPrivilegeScopes().containsKey("SQR"));
        existingPriceItemDto.setRequestStatusName(request.getRequestStatus().getName());
        existingPriceItemDto.setCurrencyCode(request.getCurrency().getCode());
        existingPriceItemDto.setCurrencyDesc(request.getCurrency().getName());
        existingPriceItemDto.setPurposeDescription(requestItem.getPurposeDescription());
        existingPriceItemDto.setUnitCode(requestItem.getUnit().getCode());
        existingPriceItemDto.setPurchaserName(firstName != null ? firstName : "" + " " + lastName != null ? lastName : "");
        existingPriceItemDto.setDeletionReason(requestItem.getDeletionReason());
        existingPriceItemDto.setCancellationReason(requestItem.getCancellationReason());

        if (requestItemAttachmentList != null && !requestItemAttachmentList.isEmpty()) {
            existingPriceItemDto.setRequestItemAttachmentList(RequestItemAttachmentMapper.INSTANCE.toRequestItemAttachmentDtoList(requestItemAttachmentList));
        }

        if (existingPriceItem != null) {
            String materialCode = StringUtils.isNotBlank(existingPriceItem.getMaterialCode()) ? existingPriceItem.getMaterialCode() : "-";
            String itemName = StringUtils.isNotBlank(existingPriceItem.getItemName()) ? existingPriceItem.getItemName() : "-";
            String itemNameDescription = StringUtils.isNotBlank(existingPriceItem.getItemDescription()) ? HtmlEscape.unescapeHtml(existingPriceItem.getItemDescription()) : "-";
            String brand = StringUtils.isNotBlank(existingPriceItem.getBrand()) ? existingPriceItem.getBrand() : "-";
            String partNo = StringUtils.isNotBlank(existingPriceItem.getPartNo()) ? existingPriceItem.getPartNo() : "-";
            String comment = StringUtils.isNotBlank(existingPriceItem.getComment()) ? existingPriceItem.getComment() : "-";

            if (requestItem.getSourcingType().getRecId() == SOURCING_TYPE_EXISTING_PRICE.id() || requestItem.getSourcingType().getRecId() == SOURCING_TYPE_EXCEPTIONAL_SOURCING.id() ||
                    (requestItem.getSourcingType().getRecId() == SOURCING_TYPE_ERFX.id() &&
                            requestItem.getSourcingStatus().getRecId() == SOURCING_QUALIFIED_SUPPLIER.id())) {

                if (requestItem.getSourcingType().getRecId() == SOURCING_TYPE_EXISTING_PRICE.id() || requestItem.getSourcingType().getRecId() == SOURCING_TYPE_EXCEPTIONAL_SOURCING.id()) {
                    existingPriceItemDto.setExistingPriceItemUnitId(existingPriceItem.getUnit().getRecId());
                    existingPriceItemDto.setExistingPriceItemUnitCode(existingPriceItem.getUnit().getCode());
                }

                if (existingPriceItemSupplier != null) {
                    existingPriceItemDto.setExistingPriceItemUnitId(existingPriceItem.getUnit().getRecId());
                    existingPriceItemDto.setExistingPriceItemUnitCode(existingPriceItem.getUnit().getCode());
                    existingPriceItemDto.setUnitCode(existingPriceItem.getUnit().getCode());
                    existingPriceItemDto.setSupplierId(existingPriceItemSupplier.getSupplier().getRecId());

                    existingPriceItemDto.setSupplierName(
                            existingPriceItemSupplier.getSupplierFullName() == null || existingPriceItemSupplier.getSupplierFullName().isEmpty() ?
                                    (SupplierNameUtil.resolveSupplierName(existingPriceItemSupplier.getSupplier(), isShowSupplierLocalLanguage, supplierFieldName)) : existingPriceItemSupplier.getSupplierFullName());
                    existingPriceItemDto.setUnitPrice(existingPriceItemSupplier.getUnitPrice());
                }

                existingPriceItemDto.setItemName(itemName);
                existingPriceItemDto.setItemDescription(itemNameDescription);
                existingPriceItemDto.setExistingPriceItemName(itemName);
                existingPriceItemDto.setExistingPriceItemDescription(itemNameDescription);
                existingPriceItemDto.setQuantity(existingPriceItem.getQuantity());
                existingPriceItemDto.setCondition(existingPriceItem.getConditions());
                existingPriceItemDto.setExistingPriceItemId(existingPriceItem.getRecId());
                existingPriceItemDto.setMaterialCode(materialCode);
                existingPriceItemDto.setVatTypeId(existingPriceItem.getVatTypeId());
            }

            existingPriceItemDto.setBrand(brand);
            existingPriceItemDto.setPartNo(partNo);
            existingPriceItemDto.setCurrencyCode(existingPriceItem.getCurrency().getCode());
            existingPriceItemDto.setCurrencyDesc(existingPriceItem.getCurrency().getName());
            existingPriceItemDto.setComment(comment);

            if (existingPriceItemAttachmentList != null && !existingPriceItemAttachmentList.isEmpty()) {
                existingPriceItemDto.setExistingPriceItemAttachmentList(ExistingPriceItemAttachmentMapper.INSTANCE.toExistingPriceItemAttachmentDtoList(existingPriceItemAttachmentList));
            }

            if (existingPriceItemSupplier != null
                    && (existingPriceItemSupplier.getUnitPrice() != null && existingPriceItemSupplier.getUnitPrice().doubleValue() == 0)
                    && requestItem.getSourcingStatus().getRecId() != SOURCING_AWAITING_SHORTLIST.id()
                    && requestItem.getSourcingType().getRecId() == SOURCING_TYPE_ERFX.id()) {

                existingPriceItemDto.setFreeItem(true);
                if(!isShowItemNameForFreeItem) {
                    existingPriceItemDto.setItemName(null);
                    existingPriceItemDto.setItemDescription(null);
                    existingPriceItemDto.setExistingPriceItemName(null);
                    existingPriceItemDto.setExistingPriceItemDescription(null);
                }
                existingPriceItemDto.setExistingPriceItemUnitId(null);
                existingPriceItemDto.setExistingPriceItemUnitCode(null);
                existingPriceItemDto.setSupplierName(existingPriceItemSupplier.getSupplierFullName() == null || existingPriceItemSupplier.getSupplierFullName().isEmpty() ?
                        (SupplierNameUtil.resolveSupplierName(existingPriceItemSupplier.getSupplier(), isShowSupplierLocalLanguage, supplierFieldName)) : existingPriceItemSupplier.getSupplierFullName());
            } else {
                existingPriceItemDto.setFreeItem(false);
            }

            if (existingPriceItem.getExistingPriceItemSupplierList() != null && !existingPriceItem.getExistingPriceItemSupplierList().isEmpty()) {
                int requestType = request.getRequestTypeId();
                existingPriceItemDto.setExistingPriceItemSupplierList(ExistingPriceItemSupplierMapper.INSTANCE.toExistingPriceItemSupplierDtoList(requestType ,requestItem, existingPriceItem, existingPriceItem.getExistingPriceItemSupplierList(), timeZone));
            }

        } else {
            String itemName = StringUtils.isNotBlank(requestItem.getItemName()) ? requestItem.getItemName() : "-";
            String itemNameDescription = StringUtils.isNotBlank(requestItem.getItemDescription()) ? requestItem.getItemDescription() : "-";
            existingPriceItemDto.setItemName(itemName);
            existingPriceItemDto.setItemDescription(itemNameDescription);
            existingPriceItemDto.setExistingPriceItemId(0L);
            existingPriceItemDto.setMaterialCode("-");
            existingPriceItemDto.setBrand(StringUtils.isNotBlank(requestItem.getBrand()) ? requestItem.getBrand() : "-");
            existingPriceItemDto.setPartNo(StringUtils.isNotBlank(requestItem.getPartNo()) ? requestItem.getPartNo() : "-");
            existingPriceItemDto.setComment("-");
        }

        existingPriceItemDto.setCreatedDate(
                DateTimeUtil.convertTimestampByUserTimeZone(requestItem.getCreatedDate(), timeZone));
        existingPriceItemDto.setCreatedBy(requestItem.getCreatedBy());
        existingPriceItemDto.setUpdatedDate(
                DateTimeUtil.convertTimestampByUserTimeZone(requestItem.getUpdatedDate(), timeZone));
        existingPriceItemDto.setUpdatedBy(requestItem.getUpdatedBy());
        existingPriceItemDto.setBidValidityStartDate(DateTimeUtil.convertTimestampByUserTimeZone(requestItem.getBidValidityStartDate(), timeZone));
        existingPriceItemDto.setBidValidityEndDate(DateTimeUtil.convertTimestampByUserTimeZone(requestItem.getBidValidityEndDate(), timeZone));
        return existingPriceItemDto;
    }

    default ExistingPriceItemDtoV2 toExistingPriceItemDtoV2(RequestItem requestItem,
                                                            ExistingPriceItem existingPriceItem,
                                                            Request request,
                                                            Type type,
                                                            List<RequestItemPr> requestItemPrList,
                                                            List<RequestItemAttachment> requestItemAttachmentList,
                                                            RequestItemLocation requestItemLocation,
                                                            List<ExistingPriceItemAttachment> existingPriceItemAttachmentList,
                                                            ExistingPriceItemSupplier existingPriceItemSupplier,
                                                            SourcingStatusDto sourcingStatusDto,
                                                            String firstName,
                                                            String lastName,
                                                            String timeZone,
                                                            boolean isShowItemNameForFreeItem,
                                                            boolean isShowSupplierLocalLanguage,
                                                            String supplierFieldName,
                                                            ExcSourcing excSourcing) {


        ExistingPriceItemDtoV2 existingPriceItemDto = new ExistingPriceItemDtoV2();

        existingPriceItemDto.setRequestId(requestItem.getRequest().getRecId());
        existingPriceItemDto.setRequestTypeId(request.getRequestTypeId());
        existingPriceItemDto.setRequestItemId(requestItem.getRecId());
        existingPriceItemDto.setBudgetTypeName(type != null ?type.getName() : null);
        existingPriceItemDto.setItem(requestItem.getItemName());
        existingPriceItemDto.setItemDescription(requestItem.getItemDescription());
        existingPriceItemDto.setBrand(requestItem.getBrand());
        existingPriceItemDto.setPartNo(requestItem.getPartNo());
        existingPriceItemDto.setQuantity(requestItem.getQuantity());
        existingPriceItemDto.setCondition(requestItem.getConditions());

        existingPriceItemDto.setSourcingDocId(requestItem.getSourcingDocId());

        if(excSourcing != null) {
            existingPriceItemDto.setSourcingTypeId(SOURCING_TYPE_EXCEPTIONAL_SOURCING.id());
            existingPriceItemDto.setSourcingDocNo(excSourcing.getExcSourcingDocNo());
            if(requestItem.getSourcingStatus().getRecId().equals(SOURCING_REJECTED.id())){
                existingPriceItemDto.setSourcingStatus(sourcingStatusDto);
            } else {
                SourcingStatusDto excSourcingStatusDto = TenantExcSourcingStatusMapper.INSTANCE.toSourcingStatusDto(excSourcing.getTenantExcSourcingStatus());
                existingPriceItemDto.setSourcingStatus(excSourcingStatusDto);
            }

        } else {
            existingPriceItemDto.setSourcingTypeId(requestItem.getSourcingType().getRecId());
            existingPriceItemDto.setSourcingDocNo(requestItem.getSourcingDocNo());
            existingPriceItemDto.setSourcingStatus(sourcingStatusDto);
        }


        existingPriceItemDto.setSourcingTypeName(requestItem.getSourcingType().getName());
        existingPriceItemDto.setItemBudget(requestItem.getItemBudget());

        if(requestItemLocation != null) {
            existingPriceItemDto.setLocationName(requestItemLocation.getLocation().getName());
        }


        existingPriceItemDto.setUsedToCopiedToPR(!requestItemPrList.isEmpty() && requestItemPrList.size() > 0);

        existingPriceItemDto.setDisplayCopiedToPRIcon(AppUtil.getPrivilegeScopes().containsKey("SQR"));

        existingPriceItemDto.setRequestStatusName(request.getRequestStatus().getName());
        existingPriceItemDto.setCurrencyCode(request.getCurrency().getCode());
        existingPriceItemDto.setCurrencyDesc(request.getCurrency().getName());
        existingPriceItemDto.setPurposeDescription(requestItem.getPurposeDescription());
        Unit unit = requestItem.getUnit();
        OptionDto unitObj = OptionDto.builder()
                .value(unit.getRecId().toString())
                .name(unit.getCode())
                .label(unit.getName())
                .build();
        existingPriceItemDto.setUnitObj(unitObj);

        existingPriceItemDto.setPurchaser(firstName != null ? firstName : "" + " " + lastName != null ? lastName : "");
        existingPriceItemDto.setDeletionReason(requestItem.getDeletionReason());
        existingPriceItemDto.setCancellationReason(requestItem.getCancellationReason());

        if (requestItemAttachmentList != null && !requestItemAttachmentList.isEmpty()) {
            existingPriceItemDto.setRequestItemAttachmentList(RequestItemAttachmentMapper.INSTANCE.toRequestItemAttachmentDtoList(requestItemAttachmentList));
        }

        if (existingPriceItem != null) {
            String materialCode = StringUtils.isNotBlank(existingPriceItem.getMaterialCode()) ? existingPriceItem.getMaterialCode() : "-";
            String itemName = StringUtils.isNotBlank(existingPriceItem.getItemName()) ? existingPriceItem.getItemName() : "-";
            String itemNameDescription = StringUtils.isNotBlank(existingPriceItem.getItemDescription()) ? HtmlEscape.unescapeHtml(existingPriceItem.getItemDescription()) : "-";
            String brand = StringUtils.isNotBlank(existingPriceItem.getBrand()) ? existingPriceItem.getBrand() : "-";
            String partNo = StringUtils.isNotBlank(existingPriceItem.getPartNo()) ? existingPriceItem.getPartNo() : "-";
            String comment = StringUtils.isNotBlank(existingPriceItem.getComment()) ? existingPriceItem.getComment() : "-";

            if (requestItem.getSourcingType().getRecId() == SOURCING_TYPE_EXISTING_PRICE.id() || requestItem.getSourcingType().getRecId() == SOURCING_TYPE_EXCEPTIONAL_SOURCING.id() ||
                    (requestItem.getSourcingType().getRecId() == SOURCING_TYPE_ERFX.id() &&
                            requestItem.getSourcingStatus().getRecId() == SOURCING_QUALIFIED_SUPPLIER.id())) {
                if(existingPriceItemSupplier != null) {
                    unit = existingPriceItem.getUnit();
                    unitObj.setValue(unit.getRecId().toString());
                    unitObj.setName(unit.getCode());
                    unitObj.setLabel(unit.getName());
                    existingPriceItemDto.setSupplierId(existingPriceItemSupplier.getSupplier().getRecId());
                    existingPriceItemDto.setItemDescription(itemNameDescription);
                    existingPriceItemDto.setSupplierName(
                            existingPriceItemSupplier.getSupplierFullName() == null || existingPriceItemSupplier.getSupplierFullName().isEmpty() ?
                                    (SupplierNameUtil.resolveSupplierName(existingPriceItemSupplier.getSupplier(), isShowSupplierLocalLanguage, supplierFieldName)) : existingPriceItemSupplier.getSupplierFullName());
                    existingPriceItemDto.setUnitPrice(existingPriceItemSupplier.getUnitPrice());
                }
                existingPriceItemDto.setItem(itemName);
                existingPriceItemDto.setExistingPriceItemId(existingPriceItem.getRecId());
                existingPriceItemDto.setMaterialCode(materialCode);
                existingPriceItemDto.setQuantity(existingPriceItem.getQuantity());
                existingPriceItemDto.setCondition(existingPriceItem.getConditions());
            }

            existingPriceItemDto.setBrand(brand);
            existingPriceItemDto.setPartNo(partNo);
            existingPriceItemDto.setCurrencyCode(existingPriceItem.getCurrency().getCode());
            existingPriceItemDto.setCurrencyDesc(existingPriceItem.getCurrency().getName());
            existingPriceItemDto.setComment(comment);

            if (existingPriceItemAttachmentList != null && !existingPriceItemAttachmentList.isEmpty()) {
                existingPriceItemDto.setExistingPriceItemAttachmentList(ExistingPriceItemAttachmentMapper.INSTANCE.toExistingPriceItemAttachmentDtoList(existingPriceItemAttachmentList));
            }

            if (existingPriceItemSupplier != null
                    && (existingPriceItemSupplier.getUnitPrice() != null && existingPriceItemSupplier.getUnitPrice().doubleValue() == 0)
                    && requestItem.getSourcingStatus().getRecId() != SOURCING_AWAITING_SHORTLIST.id()
                    && requestItem.getSourcingType().getRecId() == SOURCING_TYPE_ERFX.id()) {

                existingPriceItemDto.setFreeItem(true);

                if(!isShowItemNameForFreeItem) {
                    existingPriceItemDto.setItem(null);
                    existingPriceItemDto.setItemDescription(null);
                }

                existingPriceItemDto.setUnitObj(null);
                existingPriceItemDto.setSupplierName(existingPriceItemSupplier.getSupplierFullName() == null || existingPriceItemSupplier.getSupplierFullName().isEmpty() ?
                        (SupplierNameUtil.resolveSupplierName(existingPriceItemSupplier.getSupplier(), isShowSupplierLocalLanguage, supplierFieldName)) : existingPriceItemSupplier.getSupplierFullName());
            } else {
                existingPriceItemDto.setFreeItem(false);
            }

            if (existingPriceItem.getExistingPriceItemSupplierList() != null && !existingPriceItem.getExistingPriceItemSupplierList().isEmpty()) {
                int requestType = request.getRequestTypeId();
                existingPriceItemDto.setExistingPriceItemSupplierList(ExistingPriceItemSupplierMapper.INSTANCE.toExistingPriceItemSupplierDtoList(requestType, requestItem, existingPriceItem, existingPriceItem.getExistingPriceItemSupplierList(), timeZone));
            }

        } else {
            String itemName = StringUtils.isNotBlank(requestItem.getItemName()) ? requestItem.getItemName() : "-";
            String itemNameDescription = StringUtils.isNotBlank(requestItem.getItemDescription()) ? requestItem.getItemDescription() : "-";

            existingPriceItemDto.setItem(itemName);
            existingPriceItemDto.setItemDescription(itemNameDescription);
            existingPriceItemDto.setExistingPriceItemId(0L);
            existingPriceItemDto.setMaterialCode("-");
            existingPriceItemDto.setBrand(StringUtils.isNotBlank(requestItem.getBrand()) ? requestItem.getBrand() : "-");
            existingPriceItemDto.setPartNo(StringUtils.isNotBlank(requestItem.getPartNo()) ? requestItem.getPartNo() : "-");
            existingPriceItemDto.setComment("-");
        }

        existingPriceItemDto.setCreatedDate(
                DateTimeUtil.convertTimestampByUserTimeZone(requestItem.getCreatedDate(), timeZone));
        existingPriceItemDto.setCreatedBy(requestItem.getCreatedBy());
        existingPriceItemDto.setUpdatedDate(
                DateTimeUtil.convertTimestampByUserTimeZone(requestItem.getUpdatedDate(), timeZone));
        existingPriceItemDto.setUpdatedBy(requestItem.getUpdatedBy());
        existingPriceItemDto.setBidValidityStartDate(DateTimeUtil.convertTimestampByUserTimeZone(requestItem.getBidValidityStartDate(), timeZone));
        existingPriceItemDto.setBidValidityEndDate(DateTimeUtil.convertTimestampByUserTimeZone(requestItem.getBidValidityEndDate(), timeZone));
        return existingPriceItemDto;
    }
}
