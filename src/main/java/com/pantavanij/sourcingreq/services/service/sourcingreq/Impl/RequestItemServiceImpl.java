package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.DelegateClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.CategoryMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.OptionDtoMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestItemMapper;
import com.pantavanij.sourcingreq.services.domain.request.ConditionSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.DelegationActiveCreateDateRequest;
import com.pantavanij.sourcingreq.services.domain.request.DelegationActiveRequest;
import com.pantavanij.sourcingreq.services.domain.request.DeleteRequestItemRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveCreateDateResponse;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveResponse;
import com.pantavanij.sourcingreq.services.domain.response.UploadMessageResponse;
import com.pantavanij.sourcingreq.services.domain.response.UploadRequestItemResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.PurchaserRole;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.unbescape.html.HtmlEscape;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_REJECT;
import static com.pantavanij.sourcingreq.services.enums.ApprovalStatus.APPROVAL_CANCELLED;
import static com.pantavanij.sourcingreq.services.enums.RequestStatus.*;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_CONDITION;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_QUANTITY;
import static com.pantavanij.sourcingreq.services.enums.SectionType.SECTION_TYPE_REQI;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_DELETED;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_NONE;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_QUALIFIED_SUPPLIER;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_REJECTED;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_DRAFT;
import static com.pantavanij.sourcingreq.services.enums.ValidatorType.VALIDATOR_OBJECT;
import static com.pantavanij.sourcingreq.services.enums.ValidatorType.VALIDATOR_REQUIRED;
import static com.pantavanij.sourcingreq.services.util.CommonUtils.mapOptionalToOptionDto;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

@RequiredArgsConstructor
@Service
public class RequestItemServiceImpl implements RequestItemService {

    private final DelegateClient delegateClient;
    private final RequestItemRepository requestItemRepository;
    private final UnitRepository unitRepository;
    private final RequestRepository requestRepository;
    private final TenantRepository tenantRepository;
    private final SourcingTypeRepository sourcingTypeRepository;
    private final SourcingStatusRepository sourcingStatusRepository;
    private final RequestItemAttachmentRepository requestItemAttachmentRepository;
    private final RequestItemLocationRepository requestItemLocationRepository;
    private final RequestItemPurposeRepository requestItemPurposeRepository;
    private final RequestItemCategoryRepository requestItemCategoryRepository;
    private final RequestItemSubCategoryRepository requestItemSubCategoryRepository;
    private final RequestItemCurrencyRepository requestItemCurrencyRepository;
    private final CategoryPurchaserRepository categoryPurchaserRepository;
    private final RequestItemPurposeService requestItemPurposeService;
    private final RequestItemCategoryService requestItemCategoryService;
    private final RequestItemSubCategoryService requestItemSubCategoryService;
    private final RequestItemCurrencyService requestItemCurrencyService;
    private final RequestItemLocationService requestItemLocationService;
    private final RequestForwarderService requestForwarderService;
    private final PurposeService purposeService;
    private final PurchaserService purchaserService;
    private final CategoryService categoryService;
    private final UaaService uaaService;
    private final LocationService locationService;
    private final RequestItemPrRepository requestItemPrRepository;
    private final TenantService tenantService;
    private final UnitService unitService;
    private final TenantSectionService tenantSectionService;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final CurrencyService currencyService;
    private final TenantSubCategoryService tenantSubCategoryService;
    private final TenantConfigService tenantConfigService;
    private final RequestItemAttachmentService requestItemAttachmentService;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final TenantSectionDetailService tenantSectionDetailService;
    private final DelegationService delegationService;
    private final TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Synchronized
    public RequestItemDto saveRequestItem(RequestItemRequest requestItemRequest) {
        RequestItemDto requestItemDto = new RequestItemDto();
        RequestItem requestItem;

        Request request = requestRepository.findRequestsByRecId(requestItemRequest.getRequestId());

        if (requestItemRequest.getRecId() == 0) {
            Unit unit = unitRepository.findByRecId(Integer.parseInt(requestItemRequest.getUnitObj().getValue()));

            Optional<RequestItem> requestItemOptional = requestItemRepository.findByRequestAndPurposeDescriptionAndItemNameAndItemDescriptionAndUnitAndDeliveryLocationAndContactNameAndPhoneAndBrandAndPartNoAndItemBudget(
                    request,
                    requestItemRequest.getPurposeDescription(),
                    requestItemRequest.getItemName(),
                    requestItemRequest.getItemDescription(),
                    unit,
                    requestItemRequest.getLocation(),
                    requestItemRequest.getContactName(),
                    requestItemRequest.getContactPhone(),
                    requestItemRequest.getBrand(),
                    requestItemRequest.getPartNo(),
                    requestItemRequest.getItemBudget());

            if (requestItemOptional.isPresent()) {
                List<BigDecimal> quantity = new LinkedList<>();
                quantity.add(requestItemRequest.getQuantity());
                quantity.add(requestItemOptional.get().getQuantity());
                BigDecimal sumQuantity = quantity.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                requestItemRepository.updateRequestItemByRecId(requestItemOptional.get().getRecId(), sumQuantity);
                requestItem = requestItemRepository.findRequestItemByRecId(requestItemOptional.get().getRecId());
            } else {
                requestItem = requestItemRepository.save(setRequestItem(requestItemRequest));
            }
        } else {
            requestItem = requestItemRepository.save(setRequestItem(requestItemRequest));
        }

        if (requestItem != null) {
            requestItemPurposeService.saveOrUpdate(
                    requestItemRequest.getPurposeObj() != null ?
                            Integer.parseInt(requestItemRequest.getPurposeObj().getValue()) : null, requestItem);
            if (requestItemRequest.getCategoryObj() != null && requestItemRequest.getCategoryObj().getValue() != null ) {
                requestItemCategoryService.saveOrUpdate(Long.parseLong(requestItemRequest.getCategoryObj().getValue()), requestItem);
            }
            if (requestItemRequest.getSubCategoryObj() != null && requestItemRequest.getSubCategoryObj().getValue() != null) {
                requestItemSubCategoryService.saveOrUpdate(Integer.parseInt(requestItemRequest.getSubCategoryObj().getValue()), requestItem);
            }
            requestItemCurrencyService.saveOrUpdate(
                    requestItemRequest.getCurrencyObj() != null ?
                            Integer.parseInt(requestItemRequest.getCurrencyObj().getValue()) : null, requestItem);

            LocationDto locationDto = null;

            if (requestItemRequest.getDeliveryLocation() != null) {
                locationDto = requestItemRequest.getDeliveryLocation();
                locationDto.setAddress(requestItemRequest.getLocation());
                locationDto.setContactName(requestItemRequest.getContactName());
                locationDto.setPhone(requestItemRequest.getContactPhone());
            }

            requestItemLocationService.saveOrUpdate(locationDto, requestItem);

            //Save Request Item Attachment
            if (null != requestItemRequest.getRequestItemAttachmentList() && !requestItemRequest.getRequestItemAttachmentList().isEmpty()) {
                requestItemAttachmentService.saveRequestItemAttachment(requestItem, requestItemRequest.getRequestItemAttachmentList());
            } else {
                requestItemAttachmentService.deleteByRequestItemId(requestItem.getRecId());
            }

            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestItemDto = RequestItemMapper.INSTANCE.toRequestItemDto(requestItem, timeZone);
        }

        // Set Assigned By
        if (request.getTenant().getRecId() == 2 && !requestForwarderService.isCurrentForwarder(request.getRecId(), AppUtil.getUserName())) {
            List<RequestPurchaser> approver = requestPurchaserRepository.findByRequest(request.getRecId());
            if (approver != null && approver.size() > 0 ) {
                request.setAssignedBy(approver.get(0).getPurchaser().getLoginId());
            } else {
                request.setAssignedBy(requestItemRequest.getPurchaser());
            }
            requestRepository.save(request);
        }
        return requestItemDto;
    }

    @Override
    public List<Long> getSourcingRequestItemByRequestId(Long requestId) {
        List<RequestItem> requestItemIdList = requestItemRepository.getSourcingRequestItemByRequestId(requestId);
        return requestItemIdList.stream()
                .map(RequestItem::getRecId) // map each RequestItem to its recId value
                .collect(Collectors.toList()); // collect the Long values to a List<Long>
    }

    @Override
    public List<RequestItemDto> getRequestItemByRequestId(Long requestId, boolean isCopiedToPR) {
        List<RequestItemDto> requestItemDtoList;
        List<RequestItem> requestItemList = requestItemRepository.getRequestItemByRequestId(requestId);

        if (isCopiedToPR) {
            requestItemList = requestItemList.stream().filter(ri -> ri.getSourcingStatus().getRecId().equals(SOURCING_QUALIFIED_SUPPLIER.id())).collect(Collectors.toList());
        }

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        requestItemDtoList = RequestItemMapper.INSTANCE.toRequestItemDtoList(requestItemList, timeZone);

        if (!requestItemDtoList.isEmpty()) {
            for (RequestItemDto requestItemDto : requestItemDtoList) {
                Unit unit = unitRepository.findByRecId(requestItemDto.getUnitId());
                requestItemDto.setUnitCode(unit.getCode());
            }
        }
        return requestItemDtoList;
    }

    @Override
    public List<RequestItemV2Dto> getRequestItemByRequestId(Long requestId) {
        List<RequestItem> requestItemList = requestItemRepository.getRequestItemByRequestId(requestId);
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        return RequestItemMapper.INSTANCE.toRequestItemV2DtoList(requestItemList, timeZone);
    }

    @Override
    public RequestItemSearchDto findByRequest(Request request, Pageable pageable) {
        Page<RequestItem> requestItem = requestItemRepository.findByRequest(request, pageable);
        int totalPage = requestItem.getTotalPages();
        long total = requestItem.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<RequestItemDto> resultList = RequestItemMapper.INSTANCE.toRequestItemDtoList(requestItem.getContent(), timeZone);

        RequestItemSearchDto requestSearchDto = new RequestItemSearchDto();
        requestSearchDto.setRequestItemDtoList(resultList);
        requestSearchDto.setTotal(total);
        requestSearchDto.setTotalPage(totalPage);
        requestSearchDto.setPageSize(resultList.size());
        return requestSearchDto;
    }

    @Override
    public RequestItemV2Dto findRequestItemByRecId(Long requestItemId) {
        RequestItemV2Dto requestItemDto;
        RequestItem requestItem = requestItemRepository.findRequestItemByRecId(requestItemId);
        if (requestItem != null) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestItemDto = RequestItemMapper.INSTANCE.toRequestItemV2Dto(requestItem, timeZone);
            requestItemDto.setPurposeObj(mapOptionalToOptionDto(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toPurposeOptionDto));
//            requestItemDto.setCategoryObj(mapOptionalToOptionDto(requestItemCategoryRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
//            requestItemDto.setSubCategoryObj(mapOptionalToOptionDto(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
            Optional<RequestItemSubCategory> requestItemSubCategoryOptional = requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
            if (requestItemSubCategoryOptional.isPresent()) {
                TenantSubCategoryDto tenantSubCategoryDto = tenantSubCategoryService.findById(requestItemSubCategoryOptional.get().getSubCategory().getId());
                if (null != tenantSubCategoryDto) {
                    requestItemDto.setPurchaser(tenantSubCategoryDto.getBuyer());
                    requestItemDto.setSubCategoryObj(tenantSubCategoryDto);
                    requestItemSubCategoryOptional.ifPresent(requestItemSubCategory -> requestItemDto.setCategoryObj(mapOptionalToOptionDto(Optional.of(requestItemSubCategory.getSubCategory().getCategory()), OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto)));
                }
            }
            requestItemDto.setCurrencyObj(mapOptionalToOptionDto(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));
            Optional<RequestItemLocation> requestItemLocation = requestItemLocationRepository.findTop1ByRequestItemId(requestItemId);
            LocationDto locationDto = requestItemLocation.map(itemLocation -> locationService.getLocationById(itemLocation.getLocation().getRecId())).orElse(null);
            if (locationDto != null) {
                requestItemDto.setDeliveryLocation(locationDto);
                requestItemDto.setLocation(locationDto.getAddress());
                requestItemDto.setContactName(locationDto.getContactName());
                requestItemDto.setContactPhone(locationDto.getPhone());
            }
        } else {
            requestItemDto = null;
        }
        return requestItemDto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRequestItemByRecId(Long requestItemId) {
        RequestItem requestItem = requestItemRepository.findRequestItemByRecId(requestItemId);

        List<RequestItemAttachment> requestItemAttachmentList = requestItemAttachmentRepository.findRequestItemAttachmentsByRequestItem(requestItem);
        if (!requestItemAttachmentList.isEmpty()) {
            requestItemAttachmentRepository.deleteRequestItemAttachmentByRequestItemId(requestItem.getRecId());
        }

        List<RequestItemCategory> requestItemCategoryList = requestItemCategoryRepository.findByRequestItem(requestItem);
        if (!requestItemCategoryList.isEmpty()) {
            requestItemCategoryRepository.deleteByRequestItem(requestItem);
        }

        List<RequestItemCurrency> requestItemCurrencyList = requestItemCurrencyRepository.findByRequestItem(requestItem);
        if (!requestItemCurrencyList.isEmpty()) {
            requestItemCurrencyRepository.deleteByRequestItem(requestItem);
        }

        List<RequestItemLocation> requestItemLocationList = requestItemLocationRepository.findByRequestItem(requestItem);
        if (!requestItemLocationList.isEmpty()) {
            requestItemLocationRepository.deleteByRequestItem(requestItem.getRecId());
        }

        List<RequestItemPr> requestItemPrList = requestItemPrRepository.findByRequestItem(requestItem);
        if (!requestItemPrList.isEmpty()) {
            requestItemPrRepository.deleteByRequestItem(requestItem);
        }

        List<RequestItemPurpose> requestItemPurposeList = requestItemPurposeRepository.findByRequestItem(requestItem);
        if (!requestItemPurposeList.isEmpty()) {
            requestItemPurposeRepository.deleteByRequestItem(requestItem);
        }

        List<RequestItemSubCategory> requestItemSubCategoryList = requestItemSubCategoryRepository.findByRequestItem(requestItem);
        if (!requestItemSubCategoryList.isEmpty()) {
            requestItemSubCategoryRepository.deleteByRequestItem(requestItem);
        }

        requestItemRepository.deleteRequestItemByRecId(requestItemId);
    }

    @Override
    public boolean deleteSourcingRequestItem(Integer tenantId, DeleteRequestItemRequest request) {
        boolean isDelete = false;

        RequestItem requestItem = requestItemRepository.findRequestItemByRecId(request.getRequestItemId());
        if (requestItem != null) {
            SourcingStatus sourcingStatus = sourcingStatusRepository.findSourcingStatusByRecId(requestItem.getSourcingStatus().getRecId());

            if (sourcingStatus.isCanReject()) {
                SourcingStatus deleteStatus = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_DELETED.id());
                if (request.getDeletionReason() != null) {
                    requestItem.setDeletionReason(!request.getDeletionReason().trim().equals("") ? request.getDeletionReason() : null);
                } else {
                    requestItem.setDeletionReason(null);
                }

                requestItem.setSourcingStatus(deleteStatus);
                requestItem.setUpdatedBy(AppUtil.getUserName());
                requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                requestItemRepository.save(requestItem);

                isDelete = true;
            }
        }
        return isDelete;
    }

    @Override
    public RequestItem getRequestItemsByCondition(Long itemId, String sourcingDocNo, String sourcingDocId) {
        return requestItemRepository.getRequestItemsByCondition(itemId, sourcingDocNo, sourcingDocId);
    }

    @Override
    public RequestItem updateRequestItem(ShortlistDto shortlist, SourcingStatus sourcingStatus, String sourcingDocNo) {
        Long itemId = shortlist.getItemId();
        String sourcingDocId = String.valueOf(shortlist.getErfxItemId());
        RequestItem requestItem = getRequestItemsByCondition(itemId, sourcingDocNo, sourcingDocId);
        if (requestItem == null) {
            throw new BusinessException(ApiMessage.E7041, ApiMessage.E7041.description());
        }

        BigDecimal quantity = shortlist.getQuantity() != null ? shortlist.getQuantity() : BigDecimal.ZERO;
        requestItem.setQuantity(quantity);
        String condition = StringUtils.isEmpty(shortlist.getCondition()) ? "" : HtmlEscape.unescapeHtml(shortlist.getCondition());
        requestItem.setConditions(condition);
        requestItem.setSourcingStatus(sourcingStatus);
        requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        requestItem.setUpdatedBy(AppUtil.getUserName());
        requestItem.setBidValidityStartDate(null != shortlist.getBidValidityStartDate() ? shortlist.getBidValidityStartDate() : requestItem.getBidValidityStartDate());
        requestItem.setBidValidityEndDate(null != shortlist.getBidValidityEndDate() ? shortlist.getBidValidityEndDate() : requestItem.getBidValidityEndDate());
        requestItem.setSourcingItemSequence(shortlist.getErfxItemOrder().intValue());
        return requestItemRepository.save(requestItem);
    }

    @Override
    public void updateRequestItemBySourcingStatus(Integer sourcingStatusId, String sourcingDocNo, Integer tenantId) {
        requestItemRepository.updateRequestItemBySourcingStatus(sourcingStatusId, sourcingDocNo, tenantId);
    }

    @Override
    public List<RequestItemDto> getRequestItemByRequestIdAndSourcingStatusId(Long requestId, Integer sourcingStatusId) {
        List<RequestItemDto> requestItemDtoList = null;
        Request request = requestRepository.findRequestByRecId(requestId);
        if (request != null) {
            SourcingStatus sourcingStatus = sourcingStatusRepository.findSourcingStatusByRecId(sourcingStatusId);
            if (sourcingStatus != null) {
                List<RequestItem> requestItemList = requestItemRepository.getRequestItemByRequestAndSourcingStatus(request, sourcingStatus);

                if (requestItemList != null && !requestItemList.isEmpty()) {
                    String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
                    requestItemDtoList = RequestItemMapper.INSTANCE.toRequestItemDtoList(requestItemList, timeZone);

                    for (RequestItemDto requestItemDto : requestItemDtoList) {
                        Unit unit = unitRepository.findByRecId(requestItemDto.getUnitId());
                        requestItemDto.setUnitCode(unit.getCode());
                    }
                }
            }
        }
        return requestItemDtoList;
    }

    @Override
    public List<RequestItemV2Dto> getRequestItemByRequestIdList(Long requestId, List<Long> requestItemsId, Integer tenantId) {
        List<RequestItem> requestItemList = requestItemRepository.getRequestItemByRequestItemsId(requestId, requestItemsId, tenantId);
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        return RequestItemMapper.INSTANCE.toRequestItemV2DtoList(requestItemList, timeZone);
    }

    @Override
    public RequestItem getRequestItemByRequestItemId(Long requestItemId) {
        return requestItemRepository.findRequestItemByRecId(requestItemId);
    }

    private UploadRequestItemResponse readExcel(Sheet sheet,
                                                Integer requestTypeId,
                                                Integer typeId,
                                                Integer rowCount,
                                                Workbook workbook,
                                                Integer organizationId)
            throws NoSuchFieldException, IllegalAccessException, ParseException {

        UploadRequestItemResponse uploadRequestItemResponse = new UploadRequestItemResponse();
        List<UploadMessageResponse> messageResponsesList = new ArrayList<>();
        List<RequestItemV2Dto> requestItemV2DtoList = new ArrayList<>();

        // --- Tenant + Template ---
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        int tenantId = tenant.getRecId();

        // --- Load reference data ---
        List<OptionDto> purposeObjs = purposeService.getPurposeByTenantIdAndSearchTerm(tenantId, "");
        List<OptionDto> categoryObjs = categoryService.getCategoryByTenantIdAndSearchTerm(tenantId, "", organizationId);
        List<OptionDto> subcategoryObjs = tenantSubCategoryService.getSubCategoryByTenantIdOptionDto(tenantId, typeId);
        List<OptionDto> unitObjs = unitService.getUnitSearchTerm(tenantId, "", organizationId);
        List<LocationDto> deliveryLocation = locationService.getLocationByTenantId(tenantId, organizationId);
        List<OptionDto> currencyDtoList = currencyService.getCurrencyByTenantId(tenantId);

        // --- ดึง Section/Field definitions โดยใช้ templateId ---
        List<TenantSectionDto> tenantSectionDtoList =
                tenantSectionService.getRequestItemFields(requestTypeId, organizationId);

        List<TenantSectionDetailDto> fields = new ArrayList<>();
        if (tenantSectionDtoList != null && !tenantSectionDtoList.isEmpty() && tenantSectionDtoList.get(0).getFields() != null) {
            fields = tenantSectionDtoList.get(0).getFields();
        }

        // --- Parse Excel ---
        int rowHeader = 0;
        int lastCellIndex = sheet.getRow(rowHeader).getLastCellNum();
        List<String> fieldNameList = new ArrayList<>();
        String displayPurchaser = "";

        for (int columnIndex = 0; columnIndex < lastCellIndex; columnIndex++) {
            String columnHeader = sheet.getRow(rowHeader).getCell(columnIndex).getStringCellValue();
            boolean isOptional = columnHeader.contains(" (Optional)");
            columnHeader = columnHeader.replace(" (Optional)", "");
            final String finalColumnHeader = columnHeader;

            // map header → fieldName จาก TenantSectionDetail
            String fieldName = fields.stream()
                    .filter(field -> finalColumnHeader.equals(field.getLabel()))
                    .map(TenantSectionDetailDto::getFieldName)
                    .findFirst()
                    .orElse(null);

            if (fieldName != null) {
                fieldNameList.add(fieldName);
            }

            // --- loop row ---
            for (int rowIndex = 0; rowIndex <= rowCount; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row.getRowNum() == 0) continue; // skip header

                RequestItemV2Dto requestItemV2Dto;
                if (columnIndex == 0) {
                    requestItemV2Dto = new RequestItemV2Dto();
                    requestItemV2DtoList.add(requestItemV2Dto);
                }
                requestItemV2Dto = requestItemV2DtoList.get(row.getRowNum() - 1);

                UploadMessageResponse messageResponses = new UploadMessageResponse();
                Cell cell = row.getCell(columnIndex);

                // ---- mapping per column header
                switch (columnHeader.trim()) {
                    case "Purpose of Request":
                        boolean isPurposeRequestRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isPurposeRequestRequired && cell == null) || (isPurposeRequestRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Purpose of Request");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), purposeObjs, ApiMessage.E7069, messageResponses, rowIndex, columnIndex, 140, false);
                        }
                        break;
                    case "Item Name":
                        boolean isItemNameRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isItemNameRequired && cell == null) || (isItemNameRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Item Name");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), null, ApiMessage.E7052, messageResponses, rowIndex, columnIndex, 40, false);
                        }
                        break;
                    case "Item Description":
                        boolean isItemDescRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isItemDescRequired && cell == null) || (isItemDescRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Item Description");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), null, ApiMessage.E7052, messageResponses, rowIndex, columnIndex, 1000, false);
                        }
                        break;
                    case "Part No.":
                        boolean isPartNoRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isPartNoRequired && cell == null) || (isPartNoRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Part No.");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), null, null, messageResponses, rowIndex, columnIndex, 255, false);
                        }
                        break;
                    case "Brand":
                        boolean isBrandRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isBrandRequired && cell == null) || (isBrandRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Brand");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), null, null, messageResponses, rowIndex, columnIndex, 255, false);
                        }
                        break;
                    case "Category":
                        setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), categoryObjs, ApiMessage.E7066, messageResponses, rowIndex, columnIndex, null, false);
                        break;
                    case "Subcategory":
                        boolean isSubcategoryRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_OBJECT.id(), SECTION_TYPE_REQI.code());
                        if ((isSubcategoryRequired && cell == null) || (isSubcategoryRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Subcategory");
                        } else {
                            boolean isSubCategoryAvailable = subcategoryObjs.stream().anyMatch(subcategory -> cell != null && cell.getStringCellValue().equalsIgnoreCase(subcategory.getName()));
                            if (!subcategoryObjs.isEmpty() && isSubCategoryAvailable) {
                                setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), subcategoryObjs, ApiMessage.E7067, messageResponses, rowIndex, columnIndex, null, false);
                                if (requestItemV2Dto.getSubCategoryObj() != null && requestItemV2Dto.getSubCategoryObj().getValue() != null) {
                                    Long subCateValue = requestItemV2Dto.getSubCategoryObj().getValue();
                                    TenantSubCategoryDto tenantSubCategoryDto = tenantSubCategoryService.findById(subCateValue);
                                    if (null != tenantSubCategoryDto) {
                                        requestItemV2Dto.setPurchaser(tenantSubCategoryDto.getBuyer());
                                        if (displayPurchaser.isEmpty()) {
                                            displayPurchaser = UserDetailServiceUtil.getFullName(tenantSubCategoryDto.getBuyer());
                                        }
                                        requestItemV2Dto.setDisplayPurchaser(displayPurchaser);
                                    }
                                    TenantCategory tenantSubCategory = tenantSubCategoryService.findBySubCategoryId(requestItemV2Dto.getSubCategoryObj().getValue());
                                    if (tenantSubCategory != null) {
                                        requestItemV2Dto.setCategoryObj(CategoryMapper.INSTANCE.toTenantCategoryDto(tenantSubCategory));
                                    }
                                }
                            } else {
                                if (!subcategoryObjs.isEmpty()) {
                                    messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7080, null, "Subcategory");
                                } else {
                                    setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), subcategoryObjs, ApiMessage.E7067, messageResponses, rowIndex, columnIndex, null, false);
                                }
                            }
                        }
                        break;
                    case "Quantity":
                        boolean isQtyRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isQtyRequired && cell == null) || (isQtyRequired && cell.getNumericCellValue() == 0.0)) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Quantity");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), null, ApiMessage.E7002, messageResponses, rowIndex, columnIndex, 10, false);
                        }
                        break;
                    case "Condition":
                        boolean isConditionRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isConditionRequired && cell == null) || (isConditionRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Condition");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), null, ApiMessage.E7052, messageResponses, rowIndex, columnIndex, 500, false);
                        }
                        break;
                    case "Unit":
                        boolean isUnitRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isUnitRequired && cell == null) || (isUnitRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Unit");
                        } else {
                            boolean isUnitAvailable = unitObjs.stream().anyMatch(unit -> cell != null && cell.getStringCellValue().equalsIgnoreCase(unit.getName()));
                            if (isUnitAvailable) {
                                setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), unitObjs, ApiMessage.E7008, messageResponses, rowIndex, columnIndex, null, false);
                            }  else {
                                messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7081, null, "Unit");
                            }
                        }
                        break;
                    case "Item Budget":
                        boolean isItemBudgetRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if (isItemBudgetRequired && cell == null) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Item Budget");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), null, null, messageResponses, rowIndex, columnIndex, null, false);
                        }
                        break;
                    case "Currency":
                        boolean isCurrencyRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isCurrencyRequired && cell == null) || (isCurrencyRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Currency");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, "currencyObj", columnHeader.trim(), currencyDtoList, ApiMessage.E7068, messageResponses, rowIndex, columnIndex, null, false);
                            if (requestItemV2Dto.getCurrencyObj() != null) {
                                if (!fieldNameList.contains("currencyObj"))
                                    fieldNameList.add("currencyObj");
                            }
                        }
                        break;
                    case "Delivery Location":
                        boolean isDeliveryLocationRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isDeliveryLocationRequired && cell == null) || (isDeliveryLocationRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Delivery Location");
                        } else {
                            boolean isDeliveryLocationAvailable = deliveryLocation.stream().anyMatch(location -> cell != null && cell.getStringCellValue().equalsIgnoreCase(location.getName()));
                            if (isDeliveryLocationAvailable) {
                                setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), deliveryLocation, ApiMessage.E7006, messageResponses, rowIndex, columnIndex, null, false);
                            } else if ( cell != null && !cell.getStringCellValue().trim().isEmpty()){
                                messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7089, null, "Delivery Location");
                            }
                        }
                        break;
                    case "Contact Name":
                        if (requestItemV2Dto.getDeliveryLocation() != null) {
                            boolean isContactNameRequired = isValidationFormulaField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code(), workbook, cell);
                            if (isContactNameRequired) {
                                messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Contact Name");
                            } else {
                                boolean isContactNameAvailable = deliveryLocation.stream().anyMatch(location -> cell != null && cell.getStringCellValue().equalsIgnoreCase(location.getContactName()));
                                if (isContactNameAvailable) {
                                    setCellStringValue(cell, isOptional, requestItemV2Dto, "contactName", columnHeader.trim(), null, null, messageResponses, rowIndex, columnIndex, 100, false);
                                    if (!fieldNameList.contains("contactName"))
                                        fieldNameList.add("contactName");
                                    if ("Other".equals(requestItemV2Dto.getDeliveryLocation().getLabel()) && requestItemV2Dto.getContactName().isEmpty())
                                        messageResponses = createMessageResponse(messageResponses, cell.getRowIndex(), cell.getColumnIndex(), ApiMessage.E7031, "contactName", "Contact Name");
                                } else {
                                    boolean isOther = row.getCell(cell.getColumnIndex() - 2).getStringCellValue().equalsIgnoreCase("Other");
                                    if (!isOther) {
                                        messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7090, null, "Contact Name");
                                    } else {
                                        setCellStringValue(cell, isOptional, requestItemV2Dto, "contactName", columnHeader.trim(), null, null, messageResponses, rowIndex, columnIndex, 100, false);
                                    }
                                }
                            }
                        }
                        break;
                    case "Contact Phone":
                        if (requestItemV2Dto.getDeliveryLocation() != null) {
                            boolean isValid = validationContactPhoneFormat(workbook,cell);
                            if (isValid) {
                                messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7094, null, "Contact Phone");
                            }

                            boolean isContactPhoneRequired = isValidationFormulaField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code(), workbook, cell);
                            if (isContactPhoneRequired) {
                                messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Contact Phone");
                            } else {
                                boolean isContactPhoneAvailable = deliveryLocation
                                        .stream()
                                        .anyMatch(location -> {
                                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                            CellType cellType = CellType.forInt(cell.getCellType());
                                            CellValue cellValue = evaluator.evaluate(cell);

                                            if (cellType == CellType.FORMULA) {
                                                if (cellValue == null) {
                                                    return false;
                                                } else if (cellValue != null && cellValue.getCellTypeEnum() == STRING ) {
                                                    String phoneString = cellValue.getStringValue().trim();
                                                    if (phoneString.equals("-") ||
                                                            phoneString.equalsIgnoreCase(location.getPhone()) ||
                                                            isContactPhoneRequired && phoneString.equalsIgnoreCase(location.getPhone())
                                                    ) {
                                                        return true;
                                                    } else {
                                                        return false;
                                                    }
                                                } else if (cellValue != null && cellValue.getCellTypeEnum() == NUMERIC){
                                                    String phoneNumber = String.valueOf(cellValue.getNumberValue()).trim();
                                                    if (phoneNumber.equalsIgnoreCase(location.getPhone()) ||
                                                            isContactPhoneRequired && phoneNumber.equalsIgnoreCase(location.getPhone())) {
                                                        return true;
                                                    } else {
                                                        return false;
                                                    }
                                                }
                                            }
                                            return true;
                                        });

                                if (isContactPhoneAvailable) {
                                    setCellStringValue(cell, isOptional, requestItemV2Dto, "contactPhone", columnHeader.trim(), null, null, messageResponses, rowIndex, columnIndex, 50, false);
                                    if (!fieldNameList.contains("contactPhone"))
                                        fieldNameList.add("contactPhone");
                                    if ("Other".equals(requestItemV2Dto.getDeliveryLocation().getLabel()) && "".equals(requestItemV2Dto.getContactPhone()))
                                        messageResponses = createMessageResponse(messageResponses, cell.getRowIndex(), cell.getColumnIndex(), ApiMessage.E7031, "contactPhone", "Contact Phone");
                                } else {
                                    messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7091, null, "Contact Phone");
                                }
                            }
                        }
                        break;
                    case "Location":
                        if (requestItemV2Dto.getDeliveryLocation() != null) {
                            boolean isLocationRequired = isValidationFormulaField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code(), workbook, cell);
                            if (isLocationRequired) {
                                messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Location");
                            } else {
                                setCellStringValue(cell, isOptional, requestItemV2Dto, "location", columnHeader.trim(), null, null, messageResponses, rowIndex, columnIndex, 500, false);
                                if (!fieldNameList.contains("location"))
                                    fieldNameList.add("location");
                                if ("Other".equals(requestItemV2Dto.getDeliveryLocation().getLabel()) && "".equals(requestItemV2Dto.getLocation()))
                                    messageResponses = createMessageResponse(messageResponses, cell.getRowIndex(), cell.getColumnIndex(), ApiMessage.E7031, "location", "location");
                            }
                        }
                        break;
                    case "Bid Validity Start Date":
                        boolean isStartDateValidFormat = validationDateFormat(cell);
                        boolean isBidValidatyStartDateRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isBidValidatyStartDateRequired && cell == null) || (isBidValidatyStartDateRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Bid Validity Start Date");

                            if (!isStartDateValidFormat) {
                                messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7093, null, "Bid Validity Start Date");
                            }
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), null, ApiMessage.E7093, messageResponses, rowIndex, columnIndex, null, true);
                        }

                        break;
                    case "Bid Validity End Date":
                        boolean isEndDateValidFormat = validationDateFormat(cell);
                        boolean isBidValidatyEndtDateRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isBidValidatyEndtDateRequired && cell == null) || (isBidValidatyEndtDateRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Bid Validity End Date");

                            if (!isEndDateValidFormat) {
                                messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7093, null, "Bid Validity End Date");
                            }
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, fieldNameList.get(columnIndex), columnHeader.trim(), null, ApiMessage.E7093, messageResponses, rowIndex, columnIndex, null, true);
                            validateEndDateGreaterThanStartDate(messageResponses, cell, row, columnIndex, fieldNameList.get(columnIndex));
                        }
                        break;
                    case "Purchaser":
                        boolean isPurchaserRequired = isValidationField(tenantId, fieldNameList.get(columnIndex), VALIDATOR_REQUIRED.id(), SECTION_TYPE_REQI.code());
                        if ((isPurchaserRequired && cell == null) || (isPurchaserRequired && (cell != null && checkIsEmptyExcelStringAndNumber(cell)))) {
                            messageResponses = createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7052, null, "Bid Validity End Date");
                        } else {
                            setCellStringValue(cell, isOptional, requestItemV2Dto, "purchaser", columnHeader.trim(), null, null, messageResponses, rowIndex, columnIndex, null, false);
                        }
                        break;
                    default:
                        return throwMessageResponse(ApiMessage.E7058);

                }

                requestItemV2Dto.setItemSequence(rowIndex);
                if (messageResponses.getCode() != null) {
                    messageResponsesList.add(messageResponses);
                }
            }
        }

        // --- Extra validations (e.g., same purchaser) ---
        String selectedPurchaser = requestItemV2DtoList.get(0).getPurchaser();
        if (selectedPurchaser != null && requestItemV2DtoList.size() > 1) {
            int idx = 0;
            for (RequestItemV2Dto item : requestItemV2DtoList) {
                idx++;
                if (item.getPurchaser() != null && !selectedPurchaser.equalsIgnoreCase(item.getPurchaser())) {
                    messageResponsesList.add(createMessageResponse(new UploadMessageResponse(),
                            idx, null, ApiMessage.E7073, null, "subcategory"));
                }
            }
        }

        // --- Final response ---
        if (messageResponsesList.isEmpty()) {
            uploadRequestItemResponse.setValid(true);
            uploadRequestItemResponse.setItemV2DtoList(
                    aggregateItems(requestItemV2DtoList, fieldNameList, requestTypeId)
            );
        } else {
            messageResponsesList.sort(Comparator.comparingInt(UploadMessageResponse::getRow)
                    .thenComparing(UploadMessageResponse::getColumn, Comparator.nullsFirst(Comparator.naturalOrder())));
            uploadRequestItemResponse.setValid(false);
            uploadRequestItemResponse.setMessageResponses(messageResponsesList);
        }
        return uploadRequestItemResponse;
    }


    public boolean checkIsEmptyExcelStringAndNumber(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                String valueString = cell.getStringCellValue().trim();
                if (StringUtils.isEmpty(valueString)) {
                    return true;
                }
                break;

            case NUMERIC:
                Double valueNumber = cell.getNumericCellValue();
                if (valueNumber == null) {
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean isValidationField(Integer tenantId, String fieldName, Integer validatorId, String sectionType) {
        return tenantSectionDetailService.isRequiredField(tenantId, fieldName, validatorId, sectionType);
    }

    public boolean isValidationFormulaField(Integer tenantId, String fieldName, Integer validatorId, String sectionType, Workbook workbook, Cell cell) {
        boolean isRequired = tenantSectionDetailService.isRequiredField(tenantId, fieldName, validatorId, sectionType);

        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        CellType cellType = CellType.forInt(cell.getCellType());
        CellValue cellValue = evaluator.evaluate(cell);
        if (cellType == CellType.FORMULA) {
            if (isRequired && cellValue == null) {
                return true;
            } else {
                switch (cellValue.getCellTypeEnum()) {
                    case STRING:
                        return (isRequired && cell.getStringCellValue().trim().isEmpty());
                    case NUMERIC:
                        return (isRequired && (cell.getNumericCellValue() == 0 || cell.getNumericCellValue() == 0.0));
                    default:
                        return false;
                }
            }

        } else {
            if (isRequired && cellValue == null) {
                return true;
            } else {
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        return (isRequired && cell.getStringCellValue().trim().isEmpty());
                    case NUMERIC:
                        return (isRequired && (cell.getNumericCellValue() == 0 || cell.getNumericCellValue() == 0.0));
                    default:
                        return false;
                }
            }
        }
    }

    public boolean validationContactPhoneFormat(Workbook workbook, Cell cell) {
        final String VALID_PATTERN = ".*[-#,].*";
        final String NUMERIC_PATTERN = "\\d+";

        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        CellType cellType = CellType.forInt(cell.getCellType());
        CellValue cellValue = evaluator.evaluate(cell);
        if (cellType == CellType.FORMULA) {
            if (cellValue == null) {
                return false;
            } else {
                if (cellValue.getCellTypeEnum() == STRING) {
                    if ("-".equals(cell.getStringCellValue().trim()) ||
                            (cell.getStringCellValue().trim().matches(VALID_PATTERN) || cell.getStringCellValue().trim().matches(NUMERIC_PATTERN))
                    ) {
                        return false;
                    } else {
                        return true;
                    }
                } else if (cellValue.getCellTypeEnum() == NUMERIC) {
                    return false;
                }
            }
        } else {
            if (cellValue == null) {
                return false;
            } else {
                if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
                    if ("-".equals(cell.getStringCellValue().trim()) ||
                            (cell.getStringCellValue().trim().matches(VALID_PATTERN) || cell.getStringCellValue().trim().matches(NUMERIC_PATTERN))
                    ) {
                        return false;
                    } else {
                        return true;
                    }
                } else if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean validationDateFormat(Cell cell) {
        if (cell != null) {
            switch (cell.getCellTypeEnum()) {
                case STRING:
                    return DateTimeUtil.isValidDateString(cell.getStringCellValue().trim(), "dd/MM/yyyy");
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String dateStr = sdf.format(date);
                        return DateTimeUtil.isValidDateString(dateStr, "dd/MM/yyyy");
                    }
                    break;
            }
        }
        return false;
    }


    public List<RequestItemV2Dto> aggregateItems(List<RequestItemV2Dto> requestItemV2DtoList, List<String> keys, Integer requestTypeId) throws NoSuchFieldException, IllegalAccessException {
        Field[] keyField = new Field[keys.size()];
        for (int i = 0; i < keyField.length; i++) {
            if (keys.get(i) != "quantity") {
                keyField[i] = RequestItemV2Dto.class.getDeclaredField(keys.get(i));
                keyField[i].setAccessible(true);
            }
        }
        LinkedHashMap<String, RequestItemV2Dto> itemMap = new LinkedHashMap<>();

        for (RequestItemV2Dto item : requestItemV2DtoList) {
            StringBuilder keyBuilder = new StringBuilder();
            for (Field field : keyField) {
                Object value = field.get(item);
                keyBuilder.append(value != null ? value.toString() : "").append("-");
            }
            String keyString = keyBuilder.toString();
            if (itemMap.containsKey(keyString)) {
                if (requestTypeId != REQUEST_TYPE_CONDITION.id()) {
                    RequestItemV2Dto existingItem = itemMap.get(keyString);
                    existingItem.setQuantity(existingItem.getQuantity().add(item.getQuantity()));
                }
            } else {
                itemMap.put(keyString, item);
            }
        }
        List<RequestItemV2Dto> result = new ArrayList<>(itemMap.values());
        return result;
    }

    public UploadRequestItemResponse throwMessageResponse(ApiMessage apiMessage) {
        UploadRequestItemResponse uploadRequestItemResponse = new UploadRequestItemResponse();
        List<UploadMessageResponse> messageResponses = new ArrayList<>();
        UploadMessageResponse messageResponse = new UploadMessageResponse();
        createMessageResponse(messageResponse, null, null, apiMessage, null, null);
        messageResponses.add(messageResponse);
        uploadRequestItemResponse.setMessageResponses(messageResponses);
        uploadRequestItemResponse.setValid(false);
        return uploadRequestItemResponse;
    }

    public UploadMessageResponse createMessageResponse(UploadMessageResponse messageResponse, Integer rowNum, Integer columnNum, ApiMessage apiMessage, String fieldName, String displayColumn) {
        messageResponse.setCode(apiMessage);
        messageResponse.setRow(rowNum);
        messageResponse.setColumn(null != displayColumn ? StringUtils.capitalize(displayColumn) : null);
        if (fieldName != null) {
            messageResponse.setDescription(String.format(ApiMessage.E7031.description(), fieldName));
        } else {
            messageResponse.setDescription(apiMessage.description());
        }
        return messageResponse;
    }

    @Override
    public UploadRequestItemResponse validateAndReadFileExcel(Integer requestTypeId,
                                                              Integer typeId,
                                                              MultipartFile file,
                                                              Tenant tenant,
                                                              Integer organizationId)
            throws IOException, InvalidFormatException, NoSuchFieldException, IllegalAccessException, ParseException {

        // 1) Validate file type
        boolean isExcelFile = "application/vnd.ms-excel".equals(file.getContentType()) ||
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType());
        if (!isExcelFile) {
            return throwMessageResponse(ApiMessage.E7048);
        }

        // 2) Validate file size
        long maxSize = 26214400;
        if (file.getSize() > maxSize) {
            return throwMessageResponse(ApiMessage.E7047);
        }

        // 3) Load workbook
        InputStream inputStream = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(inputStream);

        // 4) Resolve sheet name by requestTypeId
        String sheetName = "";
        if (requestTypeId.equals(REQUEST_TYPE_QUANTITY.id())) {
            sheetName = REQUEST_TYPE_QUANTITY.code();
        } else if (requestTypeId.equals(REQUEST_TYPE_CONDITION.id())) {
            sheetName = REQUEST_TYPE_CONDITION.code();
        }

        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return throwMessageResponse(ApiMessage.E7002);
        }

        // 5) Validate max row
        int maxRow = tenantConfigService.getMaximumUploadItem(tenant.getRecId()) - 1; // Skip header row
        int rowCount = 0;
        boolean isFirstRow = true;
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        for (Row row : sheet) {
            if (isFirstRow) { // Skip header
                isFirstRow = false;
                continue;
            }

            boolean hasData = false;
            for (Cell cell : row) {
                CellValue cellValue = evaluator.evaluate(cell);
                if (cellValue != null) {
                    switch (cellValue.getCellTypeEnum()) {
                        case NUMERIC:
                        case BOOLEAN:
                            hasData = true;
                            break;
                        case STRING:
                            if (!cellValue.getStringValue().trim().isEmpty()) {
                                hasData = true;
                            }
                            break;
                    }
                    if (hasData) break;
                }
            }

            if (rowCount > maxRow) {
                return tenant.getRecId() == 1
                        ? throwMessageResponse(ApiMessage.E7051)
                        : throwMessageResponse(ApiMessage.E7072);
            }

            if (hasData) {
                rowCount++;
            }
        }

        if (rowCount == 0) {
            return throwMessageResponse(ApiMessage.E7054);
        }

        // 6) Call readExcel with organizationId
        return readExcel(sheet, requestTypeId, typeId, rowCount, workbook, organizationId);
    }



    public <T> void setCellStringValue(Cell cell, boolean isOptional, RequestItemV2Dto requestItemV2Dto, String fieldName, String displayColumn, List<T> optionDto, ApiMessage apiMessage, UploadMessageResponse messageResponses, int rowIndex, int columnIndex, Integer length, boolean isCheckDate) throws NoSuchFieldException, IllegalAccessException {
        if (cell != null) {
            String data;
            if (cell.getCellTypeEnum() == NUMERIC) {
                if (isCheckDate) {
                    if (DateUtil.isCellDateFormatted(cell) && DateTimeUtil.isValid(cell.getDateCellValue())) {
                        data = DateTimeUtil.convertTimeStampToDateStr(cell.getDateCellValue());
                    } else {
                        createMessageResponse(messageResponses, cell.getRowIndex(), cell.getColumnIndex(), apiMessage, null, displayColumn);
                        data = "";
                    }
                } else {
                    if (fieldName.equalsIgnoreCase("partNo")) {
                        DataFormatter dataFormatter = new DataFormatter();
                        data = dataFormatter.formatCellValue(cell);
                    } else {
                        double numValue = cell.getNumericCellValue();
                        DecimalFormat formatter = new DecimalFormat("#");
                        data = formatter.format(numValue);
                    }
                }
            } else {
                if ((isCheckDate && !cell.getStringCellValue().trim().isEmpty()) && !DateTimeUtil.isValidDateString(cell.getStringCellValue(), "dd/MM/yyyy")) {
                    createMessageResponse(messageResponses, cell.getRowIndex(), cell.getColumnIndex(), apiMessage, null, displayColumn);
                    data = "";
                } else {
                    data = cell.getStringCellValue();
                }
            }
            Field field = RequestItemV2Dto.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = null;
            if (data.isEmpty()) {
                value = null;
            } else if (field.getType().equals(String.class)) {
                value = data;
            } else if (field.getType().equals(BigDecimal.class)) {
                if (cell.getCellTypeEnum() != NUMERIC) {
                    createMessageResponse(messageResponses, cell.getRowIndex(), cell.getColumnIndex(), apiMessage, null, displayColumn);
                    value = BigDecimal.valueOf(0);
                } else {
                    value = BigDecimal.valueOf(cell.getNumericCellValue());
                }
            } else if (field.getType().equals(OptionDto.class)) {
                value = optionDto.stream()
                        .filter(option -> ((OptionDto) option).getLabel().equals(data))
                        .findFirst()
                        .orElse(null);

            } else if (field.getType().equals(LocationDto.class)) {
                value = optionDto.stream()
                        .filter(option -> ((LocationDto) option).getLabel().equals(data))
                        .findFirst()
                        .orElse(null);
            } else if (field.getType().equals(SubCategory.class)) {
                int categoryId = Integer.parseInt(requestItemV2Dto.getCategoryObj().getValue());
                value = optionDto.stream()
                        .filter(option -> ((SubCategory) option).getName().equals(data) && ((SubCategory) option).getCategory().getRecId().equals(categoryId))
                        .findFirst()
                        .orElse(null);

            } else if (field.getType().equals(TenantSubCategoryDto.class)) {
                T tmp = optionDto.stream()
                        .filter(option -> ((OptionDto) option).getName().equals(data))
                        .findFirst()
                        .orElse(null);
                value = new TenantSubCategoryDto();
                if (null != tmp) {
                    ((TenantSubCategoryDto) value).setName(((OptionDto) tmp).getName());
                    ((TenantSubCategoryDto) value).setValue(Long.valueOf(((OptionDto) tmp).getValue()));
                    ((TenantSubCategoryDto) value).setLabel(((OptionDto) tmp).getLabel());
                }
            } else {
                value = null;
            }

            if (value == null && !isOptional) {
                if (fieldName.contains("unit")) {
                    fieldName = fieldName.replace("Obj", "");
                }
                createMessageResponse(messageResponses, rowIndex, columnIndex, apiMessage, null, displayColumn);
            } else if (value != null && length != null) {
                if (value instanceof String) {
                    String valueStr = (String) value;
                    if (valueStr.length() > length) {
                        createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7075, null, displayColumn);
                    }
                } else if (value instanceof BigDecimal) {
                    if (value.toString().length() > length) {
                        createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7075, null, displayColumn);
                    }
                }
            }
            field.set(requestItemV2Dto, value);

        } else if (!isOptional) {
            if (cell == null) {
                if (fieldName.contains("unit")) {
                    fieldName = fieldName.replace("Obj", "");
                }
                createMessageResponse(messageResponses, rowIndex, columnIndex, apiMessage, null, displayColumn);
            } else {
                createMessageResponse(messageResponses, rowIndex, columnIndex, ApiMessage.E7031, fieldName, displayColumn);
            }
        }
    }


    @Override
    public List<ExcelRequestItemDto> searchRequestItemExcelByCondition(RequestSearchRequest searchRequest) {
        String condition = this.getRequestExcelByCondition(searchRequest);
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<ExcelRequestItemDto> excelDataList = new ArrayList<>();
        List<Object[]> objects = requestRepository.getExcelDataByCondition(condition);
        for (Object[] obj : objects) {
            ExcelRequestItemDto excelData = new ExcelRequestItemDto();
            excelData.setRequestNo(getStringValue(obj, 0));
            excelData.setRequestTypeId(getIntegerValue(obj, 1));
            excelData.setRequestName(getStringValue(obj, 2));
            excelData.setProjectCode(getStringValue(obj, 3));
            excelData.setProjectName(getStringValue(obj, 4));
            excelData.setDepartmentName(getStringValue(obj, 5));
            excelData.setBudgetRefNo(getStringValue(obj, 6));
            excelData.setBudget(getBigDecimalValue(obj, 7));
            excelData.setRequestDate(getTimestampValue(obj, 8, timeZone));
            excelData.setExpectedDate(getTimestampValue(obj, 9, timeZone));
            excelData.setCreatedBy(getStringValue(obj, 10));
            excelData.setRequestStatus(getStringValue(obj, 11));
            excelData.setSourcingTypeId(getIntegerValue(obj, 12));
            excelData.setSourcingTypeName(getStringValue(obj, 13));
            excelData.setSourcingDocNo(getStringValue(obj, 14));
            excelData.setPurposeDescription(getStringValue(obj, 15));
            excelData.setItemName(getStringValue(obj, 16));
            excelData.setExistingPriceItemName(getStringValue(obj, 17));
            excelData.setItemDescription(getStringValue(obj, 18));
            excelData.setExistingPriceItemDescription(getStringValue(obj, 19));
            excelData.setBrand(getStringValue(obj, 20));
            excelData.setPartNo(getStringValue(obj, 21));
            excelData.setQuantity(getBigDecimalValue(obj, 22));
            excelData.setConditions(getStringValue(obj, 23));
            excelData.setUnitCode(getStringValue(obj, 24));
            excelData.setItemBudget(getBigDecimalValue(obj, 25));
            excelData.setUnitPrice(getBigDecimalValue(obj, 26));
            excelData.setCurrencyCode(getStringValue(obj, 27));
            // excelData.setVatType(getStringValue(obj, 28));
            excelData.setSupplierName(getStringValue(obj, 29));
            excelData.setEXSupplierName(getStringValue(obj, 30));
            excelData.setDeliveryLocation(getStringValue(obj, 31));
            excelData.setLocationName(getStringValue(obj, 32));
            excelData.setContactName(getStringValue(obj, 33));
            excelData.setPhone(getStringValue(obj, 34));
             excelData.setCategoryName(getStringValue(obj, 35));
            excelData.setSubCategoryName(getStringValue(obj, 36));
            excelData.setAssignedBy(getStringValue(obj, 37));
            excelData.setSourcingStatusId(getIntegerValue(obj, 38));
            excelData.setSourcingStatusName(getStringValue(obj, 39));
            excelData.setLocationId(getIntegerValue(obj, 40));
            excelData.setEXUnitCode(getStringValue(obj, 41));
            excelData.setRequestItemId(getIntegerValue(obj, 42));
            excelData.setTypeCode(getStringValue(obj, 43));
            excelData.setTypeName(getStringValue(obj, 44));
            excelData.setObjective(getStringValue(obj, 45));
            excelData.setObjectiveCode(getStringValue(obj, 46));
            excelData.setObjectiveName(getStringValue(obj, 47));
            excelData.setOrganizationId(getIntegerValue(obj, 48));
            excelData.setBidStartDate(getTimestampValue(obj, 49, timeZone));
            excelData.setBidCompleteDate(getTimestampValue(obj, 50, timeZone));
            excelData.setBidCompleteMonth(getStringValue(obj, 51));
            excelData.setBidCompleteYear(getStringValue(obj, 52));
            excelData.setBidNo(getStringValue(obj, 53));
            excelData.setBiddingType(getStringValue(obj, 54));
            excelData.setBidDescription(getStringValue(obj, 55));
            excelData.setAwardedVendorName(getStringValue(obj, 56));
            excelData.setTaxNo(getStringValue(obj, 57));
            excelData.setVatType(getStringValue(obj, 58));
            excelData.setTotalProjectedPrice(getBigDecimalValue(obj, 59));
            excelData.setTotalProjectedPriceVat7Percentage(getBigDecimalValue(obj, 60));
            excelData.setTotalFinalPrice(getBigDecimalValue(obj, 61));
            excelData.setTotalFinalPriceVat7Percentage(getBigDecimalValue(obj, 62));
            excelData.setTotalSavingAmount(getBigDecimalValue(obj, 63));
            excelData.setTotalSavingAmountVat7Percentage(getBigDecimalValue(obj, 64));
            excelData.setCostAvoidanceVat7Percentage(getBigDecimalValue(obj, 65));
            excelData.setSavingPercentage(getBigDecimalValue(obj, 66));
//            excelData.setCategoryName(getStringValue(obj, 67));
            excelData.setBuyerName(getStringValue(obj, 68));
            excelData.setRequestAdditionalDepartment(getStringValue(obj, 69));
            excelData.setBidValidityStartDate(getTimestampValue(obj, 70, timeZone));
            excelData.setBidValidityEndDate(getTimestampValue(obj, 71, timeZone));
            excelData.setDelegateActionBy(getStringValue(obj, 72));
            excelData.setSourcingItemSequence(getIntegerValue(obj, 73));
            excelData.setItemSequence(getIntegerValue(obj, 74));
            excelData.setExistingPriceComment(getStringValue(obj, 75));
            excelData.setVatTypeId(getIntegerValue(obj, 76));
            excelData.setWithdraw(getBooleanValue(obj, 77));
            excelData.setWithdrawReason(getStringValue(obj, 78));
            excelData.setCompanyCode(getStringValue(obj, 79));
            excelData.setCompanyName(getStringValue(obj, 80));
            excelData.setAwardedQuantity(getBigDecimalValue(obj, 81));
            excelData.setAwardedAmount(getStringValue(obj, 82));
            excelData.setAwardedNetAmount(getBigDecimalValue(obj, 83));
            excelData.setAwardedType(getStringValue(obj, 84));
            excelData.setAwardedValue(getStringValue(obj, 85));
            excelDataList.add(excelData);
        }
        return this.sortedData(excelDataList);
    }

    public List<ExcelRequestItemDto> sortedData(List<ExcelRequestItemDto> excelData) {
        Map<String, List<ExcelRequestItemDto>> groupedExcelData = excelData.stream()
                .collect(Collectors.groupingBy(
                        data -> data.getRequestItemId().toString()
                ));

        Map<String, List<ExcelRequestItemDto>> sortedGroupedExcelData = groupedExcelData.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(ExcelRequestItemDto::getUnitPrice, Comparator.nullsFirst(Comparator.naturalOrder()))
                                        .thenComparing(ExcelRequestItemDto::getAwardedVendorName, Comparator.nullsFirst(Comparator.naturalOrder())))
                                .collect(Collectors.toList())
                ));

        excelData = new ArrayList<>();
        sortedGroupedExcelData.values().forEach(excelData::addAll);
        return excelData;
    }

    private Boolean getBooleanValue(Object[] obj, int index) {
        return obj[index] != null ? Boolean.parseBoolean(obj[index].toString()) : null;
    }

    private String getStringValue(Object[] obj, int index) {
        return obj[index] != null ? obj[index].toString() : null;
    }

    private Integer getIntegerValue(Object[] obj, int index) {
        return obj[index] != null ? Integer.parseInt(obj[index].toString()) : null;
    }

    private BigDecimal getBigDecimalValue(Object[] obj, int index) {
        return obj[index] != null ? new BigDecimal(obj[index].toString()) : null;
    }

    private Timestamp getTimestampValue(Object[] obj, int index, String timeZone) {
        return obj[index] != null ? DateTimeUtil.getTimestampByTimeZone(Timestamp.valueOf(obj[index].toString()), timeZone) : null;
    }

    private String  getRequestExcelByCondition(RequestSearchRequest searchRequest) {
        StringBuilder condition = new StringBuilder();
        Integer tenantId = searchRequest.getTenant().getRecId();
        UserDto user = AppUtil.getUser();
        if (tenantId != null) {
            condition.append(" AND r.TenantId = ").append(tenantId);
        }

        if (AppUtil.isRequester() && !AppUtil.isPurchaser()) {
            String createdBy = AppUtil.getUserName();
            if (!org.apache.commons.lang3.StringUtils.isEmpty(createdBy)) {
                condition.append(" AND (r.CreatedBy = '").append(createdBy).append("' ");
            }
            if(tenantConfigService.isEnableDeptApprover(tenantId)) {
                if (!searchRequest.getRequestStatusList().isEmpty()) {
                    List<Integer> statusIds = searchRequest.getRequestStatusList().stream()
                            .map(RequestStatusDto::getRecId)
                            .collect(Collectors.toList());
                    if (statusIds.contains(REQUEST_AWAITING.id())) {
                        statusIds.add(REQUEST_PENDING.id());
                    }
                    String inClause = String.join(",", statusIds.toString().replaceAll("\\[|\\]", ""));
                    condition.append(" AND StatusId IN (").append(inClause).append(")) ");

                }
            } else {
                condition.append(") ");
            }

        } else if (!AppUtil.isRequester() && AppUtil.isPurchaser()) {
            List<Purchaser> purchaserList = purchaserService.getByTenantId(tenantId);
            Optional<Purchaser> purchaser = purchaserList.stream()
                    .filter(i -> i.getLoginId().equalsIgnoreCase(user.getUsername()))
                    .findFirst();

            if(purchaser.isPresent()) {
                if (purchaser.get().getRoleId() == PurchaserRole.PURCHASER_ROLE_HEAD_OF_PROCUREMENT.id()) {
                    String formattedLoginIds = purchaserList.stream()
                            .map(Purchaser::getLoginId)
                            .map(loginId -> "'" + loginId + "'")
                            .distinct()
                            .collect(Collectors.joining(", "));
                    condition.append(" AND (r.AssignedBy IN (").append(formattedLoginIds).append(") OR r.DelegateActionBy IN (").append(formattedLoginIds).append(")) ");
                } else if (purchaser.get().getRoleId() == PurchaserRole.PURCHASER_ROLE_BUYER_PURCHASER_LEAD.id()) {
                    List<Integer> purchaserCategoryIds = purchaser.get().getCategoryPurchasers().stream()
                            .map(CategoryPurchaser::getCategory)
                            .map(Category::getRecId)
                            .distinct()
                            .collect(Collectors.toList());

                    List<CategoryPurchaser> categoryPurchasers = categoryPurchaserRepository.findByCategory_RecIdIn(purchaserCategoryIds);

                    String formattedLoginIds = categoryPurchasers.stream()
                            .map(CategoryPurchaser::getPurchaser)
                            .filter(i -> i.getRoleId() != PurchaserRole.PURCHASER_ROLE_HEAD_OF_PROCUREMENT.id())
                            .map(Purchaser::getLoginId)
                            .distinct()
                            .map(loginId -> "'" + loginId + "'")
                            .collect(Collectors.joining(", "));
                    condition.append(" AND (r.AssignedBy IN (").append(formattedLoginIds).append(") OR r.DelegateActionBy IN (").append(formattedLoginIds).append(")) ");
                } else if (purchaser.get().getRoleId() == PurchaserRole.PURCHASER_ROLE_BUYER_PURCHASER.id()) {
                    condition.append(" AND (r.AssignedBy = '").append(purchaser.get().getLoginId()).append("' OR r.DelegateActionBy = '").append(purchaser.get().getLoginId()).append("') ");
                }

                if(tenantConfigService.isEnableDeptApprover(tenantId)) {
                    List<Integer> excludeStatusIds = Arrays.asList(REQUEST_PENDING.id(), REQUEST_CANCELLED.id());
                    String inClause = String.join(",", excludeStatusIds.toString().replaceAll("\\[|\\]", ""));
                    condition.append(" AND StatusId NOT IN (").append(inClause).append(") ");

                    List<Integer> excludeApprovalStatusIds = Arrays.asList(APPROVAL_CANCELLED.id());
                    String inApprovalClause = String.join(",", excludeApprovalStatusIds.toString().replaceAll("\\[|\\]", ""));
                    condition.append(" AND ApprovalStatusId NOT IN (").append(inApprovalClause).append(") ");

                }
            }
        } else if (AppUtil.isPurchaser() && AppUtil.isRequester()) {
            String createdBy = AppUtil.getUserName();
            if (!org.apache.commons.lang3.StringUtils.isEmpty(createdBy)) {
                condition.append(" AND (((r.CreatedBy = '").append(createdBy).append("' ");

            }
            if(tenantConfigService.isEnableDeptApprover(tenantId)) {
                if (!searchRequest.getRequestStatusList().isEmpty()) {
                    List<Integer> statusIds = searchRequest.getRequestStatusList().stream()
                            .map(RequestStatusDto::getRecId)
                            .collect(Collectors.toList());
                    if (statusIds.contains(REQUEST_AWAITING.id())) {
                        statusIds.add(REQUEST_PENDING.id());
                    }
                    String inClause = String.join(",", statusIds.toString().replaceAll("\\[|\\]", ""));
                    condition.append(" AND StatusId IN (").append(inClause).append(")) ");

                }
            } else {
                condition.append(") ");
            }

            List<Purchaser> purchaserList = purchaserService.getByTenantId(tenantId);
            Optional<Purchaser> purchaser = purchaserList.stream()
                    .filter(i -> i.getLoginId().equalsIgnoreCase(user.getUsername()))
                    .findFirst();

            if(purchaser.isPresent()) {
                boolean hasPurchaserRole = false;
                if (purchaser.get().getRoleId() == PurchaserRole.PURCHASER_ROLE_HEAD_OF_PROCUREMENT.id()) {
                    hasPurchaserRole = true;
                    String formattedLoginIds = purchaserList.stream()
                            .map(Purchaser::getLoginId)
                            .map(loginId -> "'" + loginId + "'")
                            .distinct()
                            .collect(Collectors.joining(", "));
                    condition.append(" OR (((r.AssignedBy IN (").append(formattedLoginIds).append(") OR r.DelegateActionBy IN (").append(formattedLoginIds).append("))) ");

                } else if (purchaser.get().getRoleId() == PurchaserRole.PURCHASER_ROLE_BUYER_PURCHASER_LEAD.id()) {
                    hasPurchaserRole = true;
                    List<Integer> purchaserCategoryIds = purchaser.get().getCategoryPurchasers().stream()
                            .map(CategoryPurchaser::getCategory)
                            .map(Category::getRecId)
                            .distinct()
                            .collect(Collectors.toList());

                    List<CategoryPurchaser> categoryPurchasers = categoryPurchaserRepository.findByCategory_RecIdIn(purchaserCategoryIds);

                    String formattedLoginIds = categoryPurchasers.stream()
                            .map(CategoryPurchaser::getPurchaser)
                            .filter(i -> i.getRoleId() != PurchaserRole.PURCHASER_ROLE_HEAD_OF_PROCUREMENT.id())
                            .map(Purchaser::getLoginId)
                            .distinct()
                            .map(loginId -> "'" + loginId + "'")
                            .collect(Collectors.joining(", "));
                    condition.append(" OR (((r.AssignedBy IN (").append(formattedLoginIds).append(") OR r.DelegateActionBy IN (").append(formattedLoginIds).append("))) ");
                } else if (purchaser.get().getRoleId() == PurchaserRole.PURCHASER_ROLE_BUYER_PURCHASER.id()) {
                    hasPurchaserRole = true;
                    condition.append(" OR (((r.AssignedBy = '").append(purchaser.get().getLoginId()).append("' OR r.DelegateActionBy = '").append(purchaser.get().getLoginId()).append("')) ");
                } else {
                    hasPurchaserRole = true;
                    List<Integer> statusIds = new ArrayList<>();
                    statusIds.add(REQUEST_DRAFT.id());
                    String inClause = String.join(",", statusIds.toString().replaceAll("\\[|\\]", ""));
                    condition.append(" OR ((r.AssignedBy IS NOT NULL) OR (r.AssignedBy IS NULL AND StatusId NOT IN (").append(inClause).append("))) ");
                }

                if(tenantConfigService.isEnableDeptApprover(tenantId)) {
                    if (hasPurchaserRole) {
                        List<Integer> excludeStatusIds = Arrays.asList(REQUEST_PENDING.id(), REQUEST_CANCELLED.id());
                        String inClause = String.join(",", excludeStatusIds.toString().replaceAll("\\[|\\]", ""));
                        condition.append(" AND StatusId NOT IN (").append(inClause).append(") ");

                        List<Integer> excludeApprovalStatusIds = Arrays.asList(APPROVAL_CANCELLED.id());
                        String inApprovalClause = String.join(",", excludeApprovalStatusIds.toString().replaceAll("\\[|\\]", ""));
                        condition.append(" AND ApprovalStatusId NOT IN (").append(inApprovalClause).append(")))) ");
                    } else {
                        condition.append(")) ");
                    }
                } else {
                    condition.append(")) ");
                }
            }
        }

        Date fromDateCondition = searchRequest.getFromDate();
        Date toDateCondition = searchRequest.getToDate();
        if (fromDateCondition != null && toDateCondition != null) {
            Timestamp fromDate = DateTimeUtil.getTimestampUTC(fromDateCondition);
            Timestamp toDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.addDate(toDateCondition, 1));
            condition.append(" AND (r.requestDate + '07:00:00.0' BETWEEN '").append(fromDate).append("' AND '").append(toDate).append("' ) ");
        } else if (fromDateCondition != null) {
            Timestamp fromDate = DateTimeUtil.getTimestampUTC(fromDateCondition);
            condition.append(" AND r.requestDate + '07:00:00.0' > '").append(fromDate).append("' ");
        } else if (toDateCondition != null) {
            Timestamp toDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.addDate(toDateCondition, 1));
            condition.append(" AND r.requestDate + '07:00:00.0' < '").append(toDate).append("' ");
        }

        if (!searchRequest.getRequestStatusList().isEmpty()) {
            List<Integer> statusIds = searchRequest.getRequestStatusList().stream()
                    .map(RequestStatusDto::getRecId)
                    .collect(Collectors.toList());

            if(!tenantConfigService.isEnableDeptApprover(tenantId)) {
                String inClause = String.join(",", statusIds.toString().replaceAll("\\[|\\]", ""));
                condition.append(" AND StatusId IN (").append(inClause).append(") ");
            }
        }

        List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
        if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
            for (ConditionSearchRequest search : conditionSearchRequestList) {
                String searchField = search.getSearchField();
                String searchValue = search.getSearchValue();

                if (!StringUtils.isEmpty(searchField) && !StringUtils.isEmpty(searchValue) && !"sourcingStatus".equalsIgnoreCase(searchField)) {
                    String[] values = searchValue.replaceAll("\\[|\\]", "").split(",");
                    for (int i = 0; i < values.length; i++) {
                        values[i] = "'" + values[i].trim() + "'";
                    }

                    String fieldName = searchField.replace("Obj", "Id");
                    if (fieldName.contains("typeId")) {
                        fieldName = "rt." + fieldName;
                    } else if (fieldName.contains("projectId")) {
                        fieldName = "rp." + fieldName.replace("Id", "Code");
                    } else if (fieldName.contains("organizationId")) {
                        fieldName = "r." + fieldName;
                    }

                    String inClause = String.join(",", values);
                    condition.append(" AND ").append(fieldName).append(" IN (").append(inClause).append(") ");
                }
            }
        }
        return this.balanceParentheses(condition.toString());
    }

    private String balanceParentheses(String sqlQuery) {
        // Count the number of opening and closing parentheses
        int openCount = 0;
        int closeCount = 0;

        // Iterate through the SQL query and count the parentheses
        for (char c : sqlQuery.toCharArray()) {
            if (c == '(') {
                openCount++;
            } else if (c == ')') {
                closeCount++;
            }
        }

        // If there are more opening parentheses, append the required number of closing parentheses
        if (openCount > closeCount) {
            StringBuilder sb = new StringBuilder(sqlQuery);
            for (int i = 0; i < (openCount - closeCount); i++) {
                sb.append(")");
            }
            return sb.toString();
        }

        return sqlQuery; // Return the original query if it's already balanced
    }

    @Override
    public boolean rejectSourcingRequestItem(Integer tenantId, DeleteRequestItemRequest request) {
        boolean isReject = false;

        RequestItem requestItem = requestItemRepository.findRequestItemByRecId(request.getRequestItemId());
        if (requestItem != null) {
            Request request_ = requestRepository.findRequestsByRecId(requestItem.getRequest().getRecId());
            UserDto user = AppUtil.getUser();
            List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
            List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request_.getRecId());

            DelegationDto delegationDto = null;
            DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
            delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
            delegationActiveRequest.setDelegatorBy(request_.getAssignedBy());

            DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
            if (delegationActiveResponse.getData() != null) {
                delegationDto = delegationActiveResponse.getData();
            }
            boolean isValidOwnerAndPermission = AppUtil.isAllowedAction(user, request_, requestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_REJECT);

            if(isValidOwnerAndPermission) {
                SourcingStatus sourcingStatus = sourcingStatusRepository.findSourcingStatusByRecId(requestItem.getSourcingStatus().getRecId());

                if (sourcingStatus.isCanReject()) {
                    SourcingStatus rejectStatus = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_REJECTED.id());
                    if (request.getDeletionReason() != null) {
                        requestItem.setDeletionReason(!request.getDeletionReason().trim().equals("") ? request.getDeletionReason() : null);
                    } else {
                        requestItem.setDeletionReason(null);
                    }

                    requestItem.setSourcingStatus(rejectStatus);
                    requestItem.setUpdatedBy(AppUtil.getUserName());
                    requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    requestItemRepository.save(requestItem);

                    Request requestSR = requestRepository.findRequestsByRecId(requestItem.getRequest().getRecId());
                    if (requestSR.getAssignedBy() != null && !requestSR.getAssignedBy().equals(AppUtil.getUserName()) && requestSR.getDelegateActionBy() == null) {
                        requestSR.setDelegateActionBy(AppUtil.getUserName());
                        String authHeader = "Bearer " + AppUtil.getJwtToken();
                        DelegationActiveCreateDateRequest delegationActiveCreateDateRequest = new DelegationActiveCreateDateRequest();
                        delegationActiveCreateDateRequest.setDelegateeBy(AppUtil.getUserName());
                        delegationActiveCreateDateRequest.setDelegatorBy(requestSR.getAssignedBy());
                        DelegationActiveCreateDateResponse delegationActiveCreateDateResponse = delegateClient.getDelegationActiveCreateDate(authHeader, delegationActiveCreateDateRequest);
                        if (delegationActiveCreateDateResponse.getCreatedDate() != null) {
//                        requestSR.setDelegateActionDate(Timestamp.valueOf(delegationActiveCreateDateResponse.getCreatedDate().toLocalDateTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
                            requestSR.setDelegateActionDate(delegationActiveCreateDateResponse.getCreatedDate());
                        } else {
                            requestSR.setDelegateActionDate(DateTimeUtil.getTimestampUTC());
                        }
                        requestRepository.save(requestSR);
                    }

                    isReject = true;
                }
            }
        }
        return isReject;
    }

    private RequestItem setRequestItem(RequestItemRequest requestItemRequest) {
        Request request = requestRepository.getById(requestItemRequest.getRequestId());
        Tenant tenant = tenantRepository.getById(requestItemRequest.getTenantId());
        Unit unit = unitRepository.findByRecId(Integer.parseInt(requestItemRequest.getUnitObj().getValue()));
        SourcingType sourcingType = sourcingTypeRepository.getById(SOURCING_TYPE_DRAFT.id());
        SourcingStatus sourcingStatus = sourcingStatusRepository.getById(SOURCING_NONE.id());

        RequestItem requestItem = new RequestItem();

        if (!requestItemRequest.getRecId().equals(0L) && requestItemRequest.getRecId() != null) {
            RequestItem requestItemHeader = requestItemRepository.findRequestItemByRecId(requestItemRequest.getRecId());
            if (requestItemHeader != null) {
                requestItem = requestItemHeader;
            } else {
                requestItem.setCreatedBy(AppUtil.getUserName());
                requestItem.setCreatedDate(DateTimeUtil.getTimestampUTC());
            }
        } else {
            requestItem.setCreatedBy(AppUtil.getUserName());
            requestItem.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }

        requestItem.setRequest(request);
        requestItem.setTenant(tenant);
        requestItem.setSourcingType(sourcingType);
        requestItem.setSourcingStatus(sourcingStatus);
        requestItem.setPurposeDescription(StringUtils.isNotBlank(requestItemRequest.getPurposeDescription()) ? requestItemRequest.getPurposeDescription() : null);
        requestItem.setItemName(StringUtils.isNotBlank(requestItemRequest.getItemName()) ? requestItemRequest.getItemName() : null);
        requestItem.setItemDescription(StringUtils.isNotBlank(requestItemRequest.getItemDescription()) ? requestItemRequest.getItemDescription() : null);
        requestItem.setItemBudget(requestItemRequest.getItemBudget());
        requestItem.setConditions(StringUtils.isNotBlank(requestItemRequest.getConditions()) ? requestItemRequest.getConditions() : null);
        requestItem.setQuantity(requestItemRequest.getQuantity());
        requestItem.setUnit(unit);
        requestItem.setDeliveryLocation(requestItemRequest.getLocation());
        requestItem.setContactName(requestItemRequest.getContactName());
        requestItem.setPhone(requestItemRequest.getContactPhone());
        requestItem.setUpdatedBy(AppUtil.getUserName());
        requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        requestItem.setBrand(StringUtils.isNotBlank(requestItemRequest.getBrand()) ? requestItemRequest.getBrand() : null);
        requestItem.setPartNo(StringUtils.isNotBlank(requestItemRequest.getPartNo()) ? requestItemRequest.getPartNo() : null);
        requestItem.setBidValidityStartDate(null != requestItemRequest.getBidValidityStartDate() ? DateTimeUtil.getTimestampUTC(requestItemRequest.getBidValidityStartDate()) : null);
        requestItem.setBidValidityEndDate(null != requestItemRequest.getBidValidityEndDate() ? DateTimeUtil.getTimestampUTC(requestItemRequest.getBidValidityEndDate()) : null);
        if (requestItemRequest.getItemSequence() == null) {
            requestItem.setItemSequence(StringUtils.isNotBlank(requestItemRequest.getNo()) ? Integer.parseInt(requestItemRequest.getNo()) : 1);
        } else {
            requestItem.setItemSequence(requestItemRequest.getItemSequence());
        }

        return requestItem;
    }

    private void validateEndDateGreaterThanStartDate(UploadMessageResponse messageResponses, Cell currentCell, Row row, int columnIndex, String fieldName) throws ParseException {
        if (null == messageResponses.getCode()) {
            // Previous cell is Bid validate start date.
            // Current cell is Bid validate end date.
            Cell previousCell = row.getCell(columnIndex - 1);
            if (null != previousCell) {
                Date startDate = null;

                try {
                    startDate = DateTimeUtil.getExcelDate(previousCell);
                } catch (Exception e) {
                    startDate = DateTimeUtil.getExcelDate(previousCell);
                }

                if (null != currentCell) {
                    Date endDate = null;
                    try {
                        endDate = DateTimeUtil.getExcelDate(currentCell);
                    } catch (Exception e) {
                        endDate = DateTimeUtil.getExcelDate(currentCell);
                    }

                    if (null != startDate &&  null != endDate) {
                        int compare = endDate.compareTo(startDate);
                        if (compare <= 0) {
                            createMessageResponse(messageResponses, currentCell.getRowIndex(), currentCell.getColumnIndex(), ApiMessage.E7077, null, fieldName);
                        }
                    }
                }
            }

        }
    }
}
