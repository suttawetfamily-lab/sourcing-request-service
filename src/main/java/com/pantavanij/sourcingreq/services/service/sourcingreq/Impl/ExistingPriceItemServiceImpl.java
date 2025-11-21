package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.DelegateClient;
import com.pantavanij.sourcingreq.services.client.ErfxClient;
import com.pantavanij.sourcingreq.services.config.ERFXConfig;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ForwardedApprover;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierWebworksDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestType;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingType;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unbescape.html.HtmlEscape;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.*;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.*;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.TENANT_APPROVAL_AWAITING;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.TENANT_APPROVAL_PARTIAL_COMPLETED;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.TENANT_REQUEST_AWAITING;
import static com.pantavanij.sourcingreq.services.util.CommonUtils.mapOptionalToOptionDto;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_EXCEPTIONAL_SOURCING;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExistingPriceItemServiceImpl implements ExistingPriceItemService {
    @Value("${base.url.ptvn.cookie}")
    String BASEURL_PTVN_COOKIE;

    private final ERFXConfig erfxConfig;
    private final ErfxClient erfxClient;
    private final ExistingPriceItemRepository existingPriceItemRepository;
    private final RequestRepository requestRepository;
    private final RequestItemRepository requestItemRepository;
    private final ExistingPriceItemAttachmentRepository existingPriceItemAttachmentRepository;
    private final TenantRepository tenantRepository;
    private final CurrencyRepository currencyRepository;
    private final SupplierRepository supplierRepository;
    private final UnitRepository unitRepository;
    private final RequestItemAttachmentRepository requestItemAttachmentRepository;
    private final SourcingStatusRepository sourcingStatusRepository;
    private final SourcingTypeRepository sourcingTypeRepository;
    private final RequestItemPrRepository requestItemPrRepository;
    private final TenantApprovalStatusRepository tenantApprovalStatusRepository;
    private final RequestItemLocationRepository requestItemLocationRepository;
    private final ExistingPriceItemSupplierRepository existingPriceItemSupplierRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final RequestItemPurposeRepository requestItemPurposeRepository;
    private final RequestItemCategoryRepository requestItemCategoryRepository;
    private final RequestItemSubCategoryRepository requestItemSubCategoryRepository;
    private final RequestItemCurrencyRepository requestItemCurrencyRepository;
    private final RequestDeptApproverService requestDeptApproverService;
    private final RequestReviewerService requestReviewerService;
    private final SourcingReferenceService sourcingReferenceService;

    private final UaaService uaaService;
    private final UnitService unitService;
    private final TenantUnitService tenantUnitService;
    private final SupplierService supplierService;
    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final WorkflowInstanceApprovalService workflowInstanceApprovalService;
    private final RequestStatusService requestStatusService;
    private final ExistingPriceItemSupplierService existingPriceItemSupplierService;
    private final LocationService locationService;
    private final TenantSubCategoryRepository tenantSubCategoryRepository;
    private final RequestForwarderService requestForwarderService;
    private final ExistingPriceItemAttachmentService existingPriceItemAttachmentService;
    private final DelegationService delegationService;
    private final DelegateClient delegateClient;
    private final TenantSourcingStatusServiceImpl tenantSourcingStatusService;
    private final ExcSourcingRepository excSourcingRepository;
    private final ExcSourcingRequestItemRepository excSourcingRequestItemRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveExistingPriceItem(ExistingPriceItemRequest existingPriceItemRequest, boolean isSubmit) {
        RequestItem requestItem = requestItemRepository.findRequestItemByRecId(existingPriceItemRequest.getRequestItemId());
        Request requestSR = requestRepository.findRequestsByRecId(requestItem.getRequest().getRecId());

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(requestSR.getAssignedBy());

        boolean isDelegationActive = false;
        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            isDelegationActive = true;
        }

        boolean isOwnerPurchaser = false;
        if (requestSR.getDelegateActionBy() != null) {
            isOwnerPurchaser = AppUtil.isPurchaser() && requestSR.getDelegateActionBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName());
        } else {
            if (isDelegationActive) {
                isOwnerPurchaser = AppUtil.isPurchaser() && requestSR.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_AWAITING.code());
            } else {
                isOwnerPurchaser = AppUtil.isPurchaser() &&
                        ((requestSR.getAssignedBy() != null && requestSR.getAssignedBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName())));
            }
        }

        if (isOwnerPurchaser) {
            if (requestItem != null) {

                ExistingPriceItem existingPrice = setExistingPriceItem(requestItem, existingPriceItemRequest, isSubmit);
                if (existingPrice != null) {
                    ExistingPriceItem existingPriceItem = existingPriceItemRepository.save(existingPrice);

                    if (existingPriceItem != null) {
                        if (existingPriceItemRequest.getSupplierObj() != null &&
                                (existingPriceItemRequest.getSupplierObj().getValue() != null && !existingPriceItemRequest.getSupplierObj().getValue().isEmpty())) {
                            int supplierId = getSupplier(existingPriceItemRequest.getSupplierObj().getValue());
                            if (supplierId != 0) {
                                Optional<Supplier> supplier = supplierRepository.findSupplierByRecId(supplierId);
                                if(supplier.isPresent()) {
                                    existingPriceItemSupplierService.saveOrUpdateExistingPrice(existingPriceItemRequest, requestSR, requestItem, supplier.get(), existingPriceItem);
                                }
                            } else {
                                throw new BusinessException(ApiMessage.E7079, ApiMessage.E7079.description());
                            }
                        }

                        // Stamp Delegation ActionBy & ActionDate
                        if (requestSR.getAssignedBy() != null && !requestSR.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) && requestSR.getDelegateActionBy() == null) {
                            requestSR.setDelegateActionBy(AppUtil.getUserName());
                            String authHeader = "Bearer " + AppUtil.getJwtToken();
                            DelegationActiveCreateDateRequest delegationActiveCreateDateRequest = new DelegationActiveCreateDateRequest();
                            delegationActiveCreateDateRequest.setDelegateeBy(AppUtil.getUserName());
                            delegationActiveCreateDateRequest.setDelegatorBy(requestSR.getAssignedBy());
                            DelegationActiveCreateDateResponse delegationActiveCreateDateResponse = delegateClient.getDelegationActiveCreateDate(authHeader, delegationActiveCreateDateRequest);
                            if (delegationActiveCreateDateResponse.getCreatedDate() != null) {
                                requestSR.setDelegateActionDate(delegationActiveCreateDateResponse.getCreatedDate());
                            } else {
                                requestSR.setDelegateActionDate(DateTimeUtil.getTimestampUTC());
                            }
                            requestRepository.save(requestSR);
                        }

                        // Save ExistingPrice Item Attachment
                        if (null != existingPriceItemRequest.getExistingPriceItemAttachmentList() && !existingPriceItemRequest.getExistingPriceItemAttachmentList().isEmpty()) {
                            existingPriceItemAttachmentService.saveExistingPriceItemAttachment(existingPriceItem, existingPriceItemRequest.getExistingPriceItemAttachmentList());
                        } else {
                            existingPriceItemAttachmentService.deleteByExistingPriceItemId(existingPriceItem.getRecId());
                        }

                        // Set SourcingType = SOURCING_TYPE_EXISTING_PRICE
                        SourcingType sourcingType = sourcingTypeRepository.findSourcingTypeByRecId(existingPriceItemRequest.getSourcingTypeId() != null ? existingPriceItemRequest.getSourcingTypeId() : SOURCING_TYPE_EXISTING_PRICE.id());
                        requestItem.setSourcingType(sourcingType);
                        requestItem.setSourcingDocNo(existingPriceItem.getSourcingDocNo());
                        if (isSubmit && (Objects.equals(existingPriceItemRequest.getSourcingTypeId(), SOURCING_TYPE_EXCEPTIONAL_SOURCING.id()))) {
                            SourcingStatus awaitingStatus = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_AWAITING_RESPONSE.id());
                            requestItem.setSourcingStatus(awaitingStatus);

                        } else if (isSubmit) {
                            // Set SourcingStatus to SOURCING_QUALIFIED_SUPPLIER
                            SourcingStatus qualifiedSupplierStatus = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_QUALIFIED_SUPPLIER.id());
                            requestItem.setSourcingStatus(qualifiedSupplierStatus);

                        } else {
                            // Set SourcingStatus to SOURCING_DRAFT
                            SourcingStatus draftStatus = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_DRAFT.id());
                            requestItem.setSourcingStatus(draftStatus);

                        }
                        requestItem.setItemBudget(existingPriceItemRequest.getItemBudget() != null ? existingPriceItemRequest.getItemBudget() : requestItem.getItemBudget());
//                        requestItem.setConditions(existingPriceItemRequest.getConditions() != null ? existingPriceItemRequest.getConditions() : existingPriceItem.getConditions());
//                        requestItem.setQuantity(existingPriceItemRequest.getQuantity() != null ? existingPriceItemRequest.getQuantity() : existingPriceItem.getQuantity());
                        requestItem.setUpdatedBy(AppUtil.getUserName());
                        requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        requestItemRepository.save(requestItem);

                        // Update Approval Status to Partial Completed
                        Request request = requestRepository.findRequestsByRecId(requestItem.getRequest().getRecId());
                        TenantApprovalStatus tenantApprovalStatus = tenantApprovalStatusRepository.findByNameAndTenant(TENANT_APPROVAL_PARTIAL_COMPLETED.code(), request.getTenant());
                        request.setApprovalStatusId(tenantApprovalStatus.getApprovalStatus().getRecId());
                        request.setApprovalStatus(tenantApprovalStatus);
                        request.setUpdatedBy(AppUtil.getUserName());
                        request.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        requestRepository.save(request);

                        return existingPriceItem.getRecId();
                    }
                }
            }
        } else {
            //Cannot convert to eRFX/Existing Price due to some of the items has been done by another users.
            throw new BusinessException(ApiMessage.E7088, ApiMessage.E7088.description());
        }

        return 0L;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteExistingPriceItem(Integer tenantId, DeleteExistingPriceItemRequest request) {
        boolean isDelete = false;
        ExistingPriceItem existingPriceItem = existingPriceItemRepository.findExistingPriceItemByRecId(request.getExistingPriceItemId());
        if (existingPriceItem != null) {
            RequestItem requestItem = requestItemRepository.findRequestItemByRecId(existingPriceItem.getRequestItem().getRecId());
            SourcingStatus sourcingStatus = sourcingStatusRepository.findSourcingStatusByRecId(requestItem.getSourcingStatus().getRecId());
            SourcingStatus deleteStatus = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_DELETED.id());

            if (sourcingStatus.isCanReject()) {
                requestItem.setDeletionReason(StringUtils.isNotBlank(request.getDeletionReason()) ? request.getDeletionReason() : null);
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
    public ExistingPriceItem getExistingPriceItemByCondition(Long requestId, Long requestItemId, Integer tenantId,  String sourcingDocNo) {
        return existingPriceItemRepository.getExistingPriceItemByRequestIdAndRequestItemIdAndNullableSourcingDocNo(requestId, requestItemId, tenantId, sourcingDocNo );
    }

    @Override
    public ExistingPriceItem updateExistingPriceItem(
            ShortlistDto shortlist,
            String unitCode,
            Long requestId,
            Long requestItemId,
            Integer tenantId,
            String awardedType,
            String sourcingDocNo
    ) {
        ExistingPriceItem existingPriceItem = getExistingPriceItemByCondition(requestId, requestItemId, tenantId, sourcingDocNo);
        String itemName = HtmlEscape.unescapeHtml(shortlist.getItemName());
        String itemDetail = HtmlEscape.unescapeHtml(shortlist.getItemDetail());
        existingPriceItem.setItemName(itemName);
        existingPriceItem.setItemDescription(itemDetail);
        Tenant tenant = tenantRepository.findTenantByRecId(tenantId);

        Optional<Unit> optionalUnit = unitService.getUnitByUnitCode(unitCode);
        Unit unit = null;
        TenantUnit tenantUnit = null;
        if (optionalUnit.isEmpty()) {
            unit = unitService.createUnit(UnitDto.builder().unitCode(unitCode).description(unitCode).build());
            tenantUnitService.createTenantUnit(TenantUnitDto.builder().tenantId(tenantId).unitId(unit.getRecId()).build());
        } else {
            unit = optionalUnit.get();
            tenantUnit = tenantUnitService.findTenantUnitByUnitIdAndTenantId(unit.getRecId(), tenantId);
            if (tenantUnit == null) {
                tenantUnitService.createTenantUnit(TenantUnitDto.builder().tenantId(tenantId).unitId(unit.getRecId()).build());
            }
        }

        existingPriceItem.setUnit(unit);
        existingPriceItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        existingPriceItem.setUpdatedBy(AppUtil.getUserName());
        existingPriceItemRepository.save(existingPriceItem);

        if (shortlist.getSuppliers() != null && !shortlist.getSuppliers().isEmpty()) {
            Optional<ExistingPriceItemSupplier> existingPriceItemSupplierOtp = existingPriceItemSupplierRepository.findFirstByExistingPriceItemRecId(existingPriceItem.getRecId());
            if (existingPriceItemSupplierOtp.isPresent()) {
                ExistingPriceItemSupplier existingPriceItemSupplier = existingPriceItemSupplierOtp.get();
                try {
                    existingPriceItemSupplierRepository.deleteByExistingPriceItemRecIdIn(Collections.singletonList(existingPriceItemSupplier.getId().getExistingPriceItemId()));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BusinessException(ApiMessage.E7100, String.format(ApiMessage.E7100.description(), "Unable to delete Existing-Price-Item-Supplier"));
                }
            }
        }

        for (ErfxSupplierDto suppliers: shortlist.getSuppliers()) {
            String supplierName = HtmlEscape.unescapeHtml(suppliers.getAwardedVendorName() != null ? suppliers.getAwardedVendorName() : suppliers.getSupplier());
            String shortName = suppliers.getShortName();
            String taxNo = suppliers.getTaxNo();
            Supplier supplier = supplierService.getSupplierByShortName(shortName, tenantId);

            if (supplier == null) {
                List<TenantInfoResponse> response = uaaService.getTenantInfo(tenant.getCode());
                boolean isSearchByInvitationCode = tenantConfigService.getIsSearchByInvitationCode(tenantId);

                SupplierWebWorkSearchByTPShortNameRequest supplierWebWorkSearchByTPShortNameRequest = new SupplierWebWorkSearchByTPShortNameRequest();
                supplierWebWorkSearchByTPShortNameRequest.setTPShortName(shortName);
                supplierWebWorkSearchByTPShortNameRequest.setInvitationCode(isSearchByInvitationCode ? (response != null && !response.isEmpty() ? response.get(0).getInvitation_code() : AppUtil.getTenantId()) : "");
                SupplierWebworksDto supplierWebworksDto = supplierService.getSupplierWebWorkByTPShortName(supplierWebWorkSearchByTPShortNameRequest);

                supplier = new Supplier();
                if (supplierWebworksDto == null) {
                    supplier.setShortName(shortName);
                    supplier.setCompanyNameEN(supplierName);
                    supplier.setCompanyNameLocal(supplierName);
                    supplier.setTaxId(taxNo);
                } else {
                    supplier.setShortName(supplierWebworksDto.getTPShortName());
                    supplier.setFullCompanyNameEN(supplierWebworksDto.getFullCompanyNameEN());
                    supplier.setFullCompanyNameLocal(supplierWebworksDto.getFullCompanyNameLocal());
                    supplier.setCompanyNameEN(supplierWebworksDto.getCompanyNameEN());
                    supplier.setCompanyNameLocal(supplierWebworksDto.getCompanyNameLocal());
                    supplier.setBranchNameEN(supplierWebworksDto.getBranchNameEN());
                    supplier.setBranchNameLocal(supplierWebworksDto.getBranchNameLocal());
                    supplier.setTaxId(supplierWebworksDto.getTaxId());
                }

                supplier.setTenant(tenant);
                supplier.setCreatedBy(AppUtil.getUserName());
                supplier.setCreatedDate(DateTimeUtil.getTimestampUTC());
                supplier = supplierService.createSupplier(supplier);

            }

            existingPriceItemSupplierService.saveOrUpdateERFX(
                    suppliers.getBasePrice(),
                    suppliers.getUnitPrice(),
                    awardedType,
                    suppliers.getAwardedValue(),
                    supplier.getRecId(),
                    taxNo,
                    existingPriceItem
            );
        }
        return existingPriceItem;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExistingPriceItemSearchDto getItemByRequestAndSourcingStatus(Request request, SourcingStatus sourcingStatus, Pageable pageable, String authCode) {
        List<ExistingPriceItemDto> existingPriceItemList = new ArrayList<>();
        List<RequestItem> requestItemList = null;
        Page<RequestItem> requestItemPage = requestItemRepository.findByRequestAndSourcingStatus(request, sourcingStatus, pageable);

        ExistingPriceItemSearchDto existingPriceItemSearchDto = new ExistingPriceItemSearchDto();
        if (requestItemPage != null && !requestItemPage.isEmpty()) {

            DelegationDto delegationDto = null;

            DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
            delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
            delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

            DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
            if (delegationActiveResponse.getData() != null) {
                delegationDto = delegationActiveResponse.getData();
            }

            requestItemList = requestItemPage.getContent();
            int totalPage = requestItemPage.getTotalPages();
            long total = requestItemPage.getTotalElements();

            if (requestItemList != null) {

                List<String> sourcingDocNos = requestItemList.stream().filter(r -> r.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) && r.getSourcingDocNo() != null).map(RequestItem::getSourcingDocNo).distinct().collect(Collectors.toList());
                List<Long> eRFXDocNums = sourcingDocNos.stream().map(Long::parseLong).collect(Collectors.toList());
                ERFXStatusRequest eRFXStatusRequest = new ERFXStatusRequest();
                eRFXStatusRequest.setErfxNum(eRFXDocNums);

//                ERFXStatusResponse response = null;
//                try {
//                    response = this.getERFXStatusList(eRFXStatusRequest, null);
//                } catch (Exception ex) {
//                    //TODO: Logging for eRFX error
//                }
                Map<String,UserDetailResponse> userDetailMap = new HashMap<>();
                for (RequestItem requestItem : requestItemList) {
//                    if (response != null &&
//                            requestItem.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) &&
//                            requestItem.getSourcingDocNo() != null) {
//                        this.getERFXSourcingStatus(response.getData(), requestItem);
//                    }
                    ExistingPriceItemDto existingPriceItemDto = existingPriceItemDetail(requestItem, authCode, null, userDetailMap, delegationDto);

                    boolean isFreeItem = existingPriceItemDto.getSupplierName() != null &&
                            (existingPriceItemDto.getUnitPrice() != null && existingPriceItemDto.getUnitPrice().compareTo(BigDecimal.ZERO) == 0);
                    boolean hasPriceItem = existingPriceItemDto.getUnitPrice() != null
                            && existingPriceItemDto.getUnitPrice().compareTo(BigDecimal.ZERO) == 1;

                    if (isFreeItem || hasPriceItem) {
                        existingPriceItemList.add(existingPriceItemDto);
                    }
                }

                requestItemRepository.saveAll(requestItemList);
                requestStatusService.updateRequestAndApprovalStatus(requestItemList, request);
            }

            existingPriceItemList = existingPriceItemList.stream()
                    .map(existingPriceItemDto -> {
                        existingPriceItemDto.setCreatedByName(UserDetailServiceUtil.getFullName(existingPriceItemDto.getCreatedBy()));
                        existingPriceItemDto.setUpdatedByName(existingPriceItemDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(existingPriceItemDto.getUpdatedBy()) : null);

                        if(existingPriceItemDto.getRequestItemAttachmentList() != null) {
                            existingPriceItemDto.getRequestItemAttachmentList().stream()
                                    .map(requestItemAttachmentDto -> {
                                        if(requestItemAttachmentDto.getAttachment().getFileURL() != null) {
                                            String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(),  requestItemAttachmentDto.getAttachment().getFileURL());
                                            try {
                                                if(requestItemAttachmentDto.getAttachment().getFileSize() == null) {
                                                    requestItemAttachmentDto.getAttachment().setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                                                }
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            requestItemAttachmentDto.getAttachment().setFileURL(fullyFileURL);
                                        }
                                        requestItemAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(requestItemAttachmentDto.getAttachment().getCreatedBy()));
                                        return requestItemAttachmentDto;
                                    }).collect(Collectors.toList());
                        }

                        if(existingPriceItemDto.getExistingPriceItemAttachmentList() != null) {
                            existingPriceItemDto.getExistingPriceItemAttachmentList().stream()
                                    .map(existingPriceItemAttachmentDto -> {
                                        existingPriceItemAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(existingPriceItemAttachmentDto.getAttachment().getCreatedBy()));
                                        return existingPriceItemAttachmentDto;
                                    }).collect(Collectors.toList());
                        }
                        return existingPriceItemDto;
                    }).collect(Collectors.toList());

            existingPriceItemSearchDto = new ExistingPriceItemSearchDto();
            existingPriceItemSearchDto.setExistingPriceItemDtoList(existingPriceItemList);
            existingPriceItemSearchDto.setTotal(total);
            existingPriceItemSearchDto.setTotalPage(totalPage);
            existingPriceItemSearchDto.setPageSize(existingPriceItemList.size());
        }
        return existingPriceItemSearchDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExistingPriceItemSearchDto getItemByRequest(Request request, Pageable pageable, String authCode, String pathUrl) {
        List<ExistingPriceItemDto> existingPriceItemList = new ArrayList<>();
        List<RequestItem> requestItemList = null;
        Page<RequestItem> requestItemPage = requestItemRepository.findByRequest(request, pageable);

        ExistingPriceItemSearchDto existingPriceItemSearchDto = new ExistingPriceItemSearchDto();
        if (requestItemPage != null && !requestItemPage.isEmpty()) {

            DelegationDto delegationDto = null;

            DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
            delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
            delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

            DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
            if (delegationActiveResponse.getData() != null) {
                delegationDto = delegationActiveResponse.getData();
            }

            requestItemList = requestItemPage.getContent();
            int totalPage = requestItemPage.getTotalPages();
            long total = requestItemPage.getTotalElements();

            if (requestItemList != null) {

                List<String> sourcingDocNos = requestItemList.stream().filter(r -> r.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) && r.getSourcingDocNo() != null).map(RequestItem::getSourcingDocNo).distinct().collect(Collectors.toList());
                List<Long> eRFXDocNums = sourcingDocNos.stream().map(Long::parseLong).collect(Collectors.toList());
                ERFXStatusRequest eRFXStatusRequest = new ERFXStatusRequest();
                eRFXStatusRequest.setErfxNum(eRFXDocNums);

//                ERFXStatusResponse response = null;
//                try {
//                    response = this.getERFXStatusList(eRFXStatusRequest, null);
//                } catch (Exception ex) {
//                    //TODO: Logging for eRFX error
//                }

                Map<String,UserDetailResponse> userDetailMap = new HashMap<>();
                for (RequestItem requestItem : requestItemList) {
//                    if (response != null &&
//                            requestItem.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) &&
//                            requestItem.getSourcingDocNo() != null) {
//                        this.getERFXSourcingStatus(response.getData(), requestItem);
//                    }
                    ExistingPriceItemDto existingPriceItemDto = existingPriceItemDetail(requestItem, authCode, pathUrl, userDetailMap, delegationDto);
                    existingPriceItemDto.setItemSequence(requestItem.getItemSequence());
                    existingPriceItemList.add(existingPriceItemDto);
                }

                requestItemRepository.saveAll(requestItemList);
                requestStatusService.updateRequestAndApprovalStatus(requestItemList, request);
            }

            existingPriceItemList = existingPriceItemList.stream()
                    .map(existingPriceItemDto -> {
                        existingPriceItemDto.setCreatedByName(UserDetailServiceUtil.getFullName(existingPriceItemDto.getCreatedBy()));
                        existingPriceItemDto.setUpdatedByName(existingPriceItemDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(existingPriceItemDto.getUpdatedBy()) : null);

                        RequestItemLocation requestItemLocation = requestItemLocationRepository.findRequestItemLocationByRequestItemRecId(existingPriceItemDto.getRequestItemId());
                        if (requestItemLocation != null) {
                            existingPriceItemDto.setLocationName(requestItemLocation.getLocation().getName());
                        }

                        if (existingPriceItemDto.getRequestItemAttachmentList() != null) {
                            existingPriceItemDto.getRequestItemAttachmentList().stream()
                                    .map(requestItemAttachmentDto -> {
                                        if(requestItemAttachmentDto.getAttachment().getFileURL() != null) {
                                            String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(),  requestItemAttachmentDto.getAttachment().getFileURL());
                                            try {
                                                if(requestItemAttachmentDto.getAttachment().getFileSize() == null) {
                                                    requestItemAttachmentDto.getAttachment().setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                                                }
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            requestItemAttachmentDto.getAttachment().setFileURL(fullyFileURL);
                                        }
                                        requestItemAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(requestItemAttachmentDto.getAttachment().getCreatedBy()));
                                        return requestItemAttachmentDto;
                                    }).collect(Collectors.toList());
                        }

                        if (existingPriceItemDto.getExistingPriceItemAttachmentList() != null) {
                            existingPriceItemDto.getExistingPriceItemAttachmentList().stream()
                                    .map(existingPriceItemAttachmentDto -> {
                                        existingPriceItemAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(existingPriceItemAttachmentDto.getAttachment().getCreatedBy()));
                                        return existingPriceItemAttachmentDto;
                                    }).collect(Collectors.toList());
                        }

                        Long requestItemId = existingPriceItemDto.getRequestItemId();
                        existingPriceItemDto.setVatTypeObj(OptionDtoMapper.INSTANCE.toVatTypeOptionDto(existingPriceItemDto.getVatTypeId()));
                        existingPriceItemDto.setSubCategoryObj(mapOptionalToOptionDto(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
                        if (existingPriceItemDto.getSubCategoryObj() != null) {
                            Optional<TenantSubCategory> optionalTenantSubCategory = tenantSubCategoryRepository.findById(Long.valueOf(existingPriceItemDto.getSubCategoryObj().getValue()));
                            if (optionalTenantSubCategory.isPresent()) {
                                Optional<TenantCategory> tenantCategory = Optional.of(optionalTenantSubCategory.get().getCategory());
                                existingPriceItemDto.setCategoryObj(mapOptionalToOptionDto(tenantCategory, OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto));
                            }
                        }

                        // Sort supplier
                        if (existingPriceItemDto.getExistingPriceItemSupplierList() != null && !existingPriceItemDto.getExistingPriceItemSupplierList().isEmpty()) {
                            List<ExistingPriceItemSupplierDto> existingPriceItemSupplierList = existingPriceItemDto.getExistingPriceItemSupplierList()
                                    .stream()
                                    .filter(supplier -> supplier != null && supplier.getSupplierFullName() != null)
                                    .sorted(Comparator.comparing(ExistingPriceItemSupplierDto::getUnitPrice,
                                                    Comparator.nullsFirst(Comparator.naturalOrder()))
                                            .thenComparing(ExistingPriceItemSupplierDto::getSupplierFullName,
                                                    Comparator.nullsFirst(Comparator.naturalOrder())))
                                    .collect(Collectors.toList());
                            existingPriceItemDto.setExistingPriceItemSupplierList(existingPriceItemSupplierList);
                        }

                        return existingPriceItemDto;
                    })
                    .sorted(Comparator.comparing(ExistingPriceItemDto::getItemSequence,
                            Comparator.nullsFirst(Comparator.naturalOrder())))
                    .collect(Collectors.toList());

            existingPriceItemSearchDto = new ExistingPriceItemSearchDto();
            existingPriceItemSearchDto.setExistingPriceItemDtoList(existingPriceItemList);
            existingPriceItemSearchDto.setTotal(total);
            existingPriceItemSearchDto.setTotalPage(totalPage);
            existingPriceItemSearchDto.setPageSize(existingPriceItemList.size());
        }
        return existingPriceItemSearchDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExistingPriceItemSearchDtoV2 getItemByRequestV2(Request request, Pageable pageable, String authCode, String pathUrl, Integer sourcingTypeId, String sourcingDocNo) {
        List<ExistingPriceItemDtoV2> existingPriceItemList = new ArrayList<>();
        List<RequestItem> requestItemList = null;
        Page<RequestItem> requestItemPage = null;
        ExcSourcing excSourcing = null;

        if (sourcingTypeId != null && StringUtils.isNotBlank(sourcingDocNo)) {
            Optional<ExcSourcing> excSourcingOpt = excSourcingRepository.findByExcSourcingDocNo(sourcingDocNo);
            if (excSourcingOpt.isPresent()) {
                excSourcing = excSourcingOpt.get();
            }

            List<Long> requestItemIds = existingPriceItemRepository
                    .findByTenantAndSourcingTypeIdAndSourcingDocNo(request.getTenant(), sourcingTypeId, sourcingDocNo)
                    .stream()
//                    // กรองเฉพาะ RequestItem ที่ไม่ถูก Reject
//                    .filter(item -> {
//                        RequestItem reqItem = item.getRequestItem();
//                        return reqItem != null
//                                && (reqItem.getSourcingStatus() == null
//                                || reqItem.getSourcingStatus().getRecId() == null
//                                || !reqItem.getSourcingStatus().getRecId().equals(SOURCING_REJECTED.id()));
//                    })
                    .map(item -> item.getRequestItem().getRecId())
                    .distinct()
                    .collect(Collectors.toList());

            if (!requestItemIds.isEmpty()) {
                requestItemPage = requestItemRepository.findByRecIdIn(requestItemIds, pageable);
            }
        } else {
            // ไม่มี sourcingDocNo → ใช้ request เดิม
            requestItemPage = requestItemRepository.findByRequest(request, pageable);
        }




        ExistingPriceItemSearchDtoV2 existingPriceItemSearchDto = new ExistingPriceItemSearchDtoV2();
        if (requestItemPage != null && !requestItemPage.isEmpty()) {

            DelegationDto delegationDto = null;

            DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
            delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
            delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

            DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
            if (delegationActiveResponse.getData() != null) {
                delegationDto = delegationActiveResponse.getData();
            }

            requestItemList = requestItemPage.getContent();
            int totalPage = requestItemPage.getTotalPages();
            long total = requestItemPage.getTotalElements();

            if (requestItemList != null) {

                List<String> sourcingDocNos = requestItemList.stream().filter(r -> r.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) && r.getSourcingDocNo() != null).map(RequestItem::getSourcingDocNo).distinct().collect(Collectors.toList());
                List<Long> eRFXDocNums = sourcingDocNos.stream().map(Long::parseLong).collect(Collectors.toList());
                ERFXStatusRequest eRFXStatusRequest = new ERFXStatusRequest();
                eRFXStatusRequest.setErfxNum(eRFXDocNums);

//                ERFXStatusResponse response = null;
//                try {
//                    response = this.getERFXStatusList(eRFXStatusRequest, null);
//                } catch (Exception ex) {
//                    // TODO: Logging for eRFX error
//                }

                Map<String,UserDetailResponse> userDetailMap = new HashMap<>();
                for (RequestItem requestItem : requestItemList) {
//                    if (response != null &&
//                            requestItem.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) &&
//                            requestItem.getSourcingDocNo() != null) {
//                        this.getERFXSourcingStatus(response.getData(), requestItem);
//                    }
                    ExistingPriceItemDtoV2 existingPriceItemDto = existingPriceItemDetailV2(requestItem, authCode, pathUrl, userDetailMap, delegationDto, excSourcing);
                    existingPriceItemList.add(existingPriceItemDto);
                }

                requestItemRepository.saveAll(requestItemList);
                requestStatusService.updateRequestAndApprovalStatus(requestItemList, request);
            }

            existingPriceItemList = existingPriceItemList.stream()
                    .peek(existingPriceItemDto -> {
                        existingPriceItemDto.setCreatedByName(UserDetailServiceUtil.getFullName(existingPriceItemDto.getCreatedBy()));
                        existingPriceItemDto.setUpdatedByName(existingPriceItemDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(existingPriceItemDto.getUpdatedBy()) : null);

                        if(existingPriceItemDto.getRequestItemAttachmentList() != null) {
                            existingPriceItemDto.getRequestItemAttachmentList().stream()
                                    .map(requestItemAttachmentDto -> {
                                        if(requestItemAttachmentDto.getAttachment().getFileURL() != null) {
                                            String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(),  requestItemAttachmentDto.getAttachment().getFileURL());
                                            try {
                                                if(requestItemAttachmentDto.getAttachment().getFileSize() == null) {
                                                    requestItemAttachmentDto.getAttachment().setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                                                }
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            requestItemAttachmentDto.getAttachment().setFileURL(fullyFileURL);
                                        }
                                        requestItemAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(requestItemAttachmentDto.getAttachment().getCreatedBy()));
                                        return requestItemAttachmentDto;
                                    }).collect(Collectors.toList());
                        }

                        if(existingPriceItemDto.getExistingPriceItemAttachmentList() != null) {
                            existingPriceItemDto.getExistingPriceItemAttachmentList().stream()
                                    .map(existingPriceItemAttachmentDto -> {
                                        existingPriceItemAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(existingPriceItemAttachmentDto.getAttachment().getCreatedBy()));
                                        return existingPriceItemAttachmentDto;
                                    }).collect(Collectors.toList());
                        }

                        Long requestItemId = existingPriceItemDto.getRequestItemId();
                        Long existingPriceItemId = existingPriceItemDto.getExistingPriceItemId();

                        existingPriceItemDto.setPurposeObj(mapOptionalToOptionDto(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toPurposeOptionDto));
                        existingPriceItemDto.setSubCategoryObj(mapOptionalToOptionDto(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));

                        if (existingPriceItemDto.getSubCategoryObj() != null) {
                            Optional<TenantSubCategory> optionalTenantSubCategory = tenantSubCategoryRepository.findById(Long.valueOf(existingPriceItemDto.getSubCategoryObj().getValue()));
                            if (optionalTenantSubCategory.isPresent()) {
                                Optional<TenantCategory> tenantCategory = Optional.of(optionalTenantSubCategory.get().getCategory());
                                existingPriceItemDto.setCategoryObj(mapOptionalToOptionDto(tenantCategory, OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto));
                            }
                        }

                        existingPriceItemDto.setCurrencyObj(mapOptionalToOptionDto(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));
                        if(existingPriceItemDto.getExistingPriceItemSupplierList() != null && existingPriceItemDto.getExistingPriceItemSupplierList().size() == 1) {
                            OptionDto supplierObj = existingPriceItemDto.getExistingPriceItemSupplierList().get(0).getSupplierObj();
                            existingPriceItemDto.setSupplierObj(supplierObj);
                        }

                        RequestItemLocation requestItemLocation = requestItemLocationRepository.findRequestItemLocationByRequestItemRecId(existingPriceItemDto.getRequestItemId());
                        LocationDto locationDto = requestItemLocation != null ? locationService.getLocationById(requestItemLocation.getLocation().getRecId()) : null;
                        if (requestItemLocation != null) {
                            existingPriceItemDto.setContactName(requestItemLocation.getContactName());
                            existingPriceItemDto.setContactPhone(requestItemLocation.getPhone());
                            existingPriceItemDto.setDeliveryLocation(getDeliveryLocation(requestItemLocation, locationDto));
                            existingPriceItemDto.setLocation(requestItemLocation.getDeliveryLocation());
                            existingPriceItemDto.setLocationName(requestItemLocation.getLocation().getName());
                        }

                        if (null != existingPriceItemDto.getPurposeObj()) {
                            existingPriceItemDto.setPurposeDescription(StringUtils.isNotBlank(existingPriceItemDto.getPurposeObj().getLabel()) ? existingPriceItemDto.getPurposeObj().getLabel() : "-");
                        }

                        // Sort supplier
                        if (existingPriceItemDto.getExistingPriceItemSupplierList() != null && !existingPriceItemDto.getExistingPriceItemSupplierList().isEmpty()) {
                            List<ExistingPriceItemSupplierDto> existingPriceItemSupplierList = existingPriceItemDto.getExistingPriceItemSupplierList()
                                    .stream()
                                    .filter(supplier -> supplier != null && supplier.getUnitPrice() != null && supplier.getSupplierFullName() != null)
                                    .sorted(Comparator.comparing(ExistingPriceItemSupplierDto::getUnitPrice,
                                                    Comparator.nullsFirst(Comparator.naturalOrder()))
                                            .thenComparing(ExistingPriceItemSupplierDto::getSupplierFullName,
                                                    Comparator.nullsFirst(Comparator.naturalOrder())))
                                    .collect(Collectors.toList());
                            existingPriceItemDto.setExistingPriceItemSupplierList(existingPriceItemSupplierList);
                        }

                    })
                    .sorted(Comparator.comparing(ExistingPriceItemDtoV2::getItemSequence,
                            Comparator.nullsFirst(Comparator.naturalOrder())))
                    .collect(Collectors.toList());

            existingPriceItemSearchDto = new ExistingPriceItemSearchDtoV2();
            existingPriceItemSearchDto.setExistingPriceItemDtoList(existingPriceItemList);
            existingPriceItemSearchDto.setTotal(total);
            existingPriceItemSearchDto.setTotalPage(totalPage);
            existingPriceItemSearchDto.setPageSize(existingPriceItemList.size());
        }
        return existingPriceItemSearchDto;
    }

    public LocationDto getDeliveryLocation(RequestItemLocation requestItemLocation, LocationDto locationDto) {
        if (requestItemLocation != null && requestItemLocation.getContactName() != null) {
            locationDto.setContactName(requestItemLocation.getContactName());
        }
        if (requestItemLocation != null && requestItemLocation.getPhone() != null) {
            locationDto.setPhone(requestItemLocation.getPhone());
        }
        if (requestItemLocation != null && requestItemLocation.getDeliveryLocation() != null) {
            locationDto.setAddress(requestItemLocation.getDeliveryLocation());
        }
        return locationDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> getAllItemByRequest(Request request) {
        List<RequestItem> requestItemList = requestItemRepository.getAllEligibleRequestItemByRequestId(request.getRecId());
        return requestItemList.stream().map(RequestItem::getRecId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ExistingPriceItemDtoV2> getAllItemByIdList(Request request, List<Long> requestItems, String pathUrl) {
        List<ExistingPriceItemDtoV2> existingPriceItemList = new ArrayList<>();
        List<RequestItem> requestItemList = requestItemRepository.getRequestItemByRequestItemIdList(requestItems);
        if (requestItemList != null && !requestItemList.isEmpty()) {

            if (requestItemList != null) {

                DelegationDto delegationDto = null;

                DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
                delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
                delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

                DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
                if (delegationActiveResponse.getData() != null) {
                    delegationDto = delegationActiveResponse.getData();
                }

                List<String> sourcingDocNos = requestItemList.stream().filter(r -> r.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) && r.getSourcingDocNo() != null).map(RequestItem::getSourcingDocNo).distinct().collect(Collectors.toList());
                List<Long> eRFXDocNums = sourcingDocNos.stream().map(Long::parseLong).collect(Collectors.toList());
                ERFXStatusRequest eRFXStatusRequest = new ERFXStatusRequest();
                eRFXStatusRequest.setErfxNum(eRFXDocNums);

//                ERFXStatusResponse response = null;
//                try {
//                    response = this.getERFXStatusList(eRFXStatusRequest, null);
//                } catch (Exception ex) {
//                    //TODO: Logging for eRFX error
//                }
                Map<String,UserDetailResponse> userDetailMap = new HashMap<>();
                for (RequestItem requestItem : requestItemList) {
//                    if (response != null &&
//                            requestItem.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) &&
//                            requestItem.getSourcingDocNo() != null) {
//                        this.getERFXSourcingStatus(response.getData(), requestItem);
//                    }
                    ExistingPriceItemDtoV2 existingPriceItemDto = existingPriceItemDetailV2(requestItem, "", pathUrl, userDetailMap, delegationDto, null);
                    existingPriceItemList.add(existingPriceItemDto);
                }

                requestItemRepository.saveAll(requestItemList);
                requestStatusService.updateRequestAndApprovalStatus(requestItemList, request);
            }

            existingPriceItemList = existingPriceItemList.stream()
                    .map(existingPriceItemDto -> {
                        existingPriceItemDto.setCreatedByName(UserDetailServiceUtil.getFullName(existingPriceItemDto.getCreatedBy()));
                        existingPriceItemDto.setUpdatedByName(existingPriceItemDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(existingPriceItemDto.getUpdatedBy()) : null);

                        RequestItemLocation requestItemLocation = requestItemLocationRepository.findRequestItemLocationByRequestItemRecId(existingPriceItemDto.getRequestItemId());
                        if(requestItemLocation != null) {
                            existingPriceItemDto.setLocationName(requestItemLocation.getLocation().getName());
                        }

                        if(existingPriceItemDto.getRequestItemAttachmentList() != null) {
                            existingPriceItemDto.getRequestItemAttachmentList().stream()
                                    .map(requestItemAttachmentDto -> {
                                        if(requestItemAttachmentDto.getAttachment().getFileURL() != null) {
                                            String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(),  requestItemAttachmentDto.getAttachment().getFileURL());
                                            try {
                                                if(requestItemAttachmentDto.getAttachment().getFileSize() == null) {
                                                    requestItemAttachmentDto.getAttachment().setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                                                }
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            requestItemAttachmentDto.getAttachment().setFileURL(fullyFileURL);
                                        }
                                        requestItemAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(requestItemAttachmentDto.getAttachment().getCreatedBy()));
                                        return requestItemAttachmentDto;
                                    }).collect(Collectors.toList());
                        }

                        if(existingPriceItemDto.getExistingPriceItemAttachmentList() != null) {
                            existingPriceItemDto.getExistingPriceItemAttachmentList().stream()
                                    .map(existingPriceItemAttachmentDto -> {
                                        existingPriceItemAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(existingPriceItemAttachmentDto.getAttachment().getCreatedBy()));
                                        return existingPriceItemAttachmentDto;
                                    }).collect(Collectors.toList());
                        }

                        Long requestItemId = existingPriceItemDto.getRequestItemId();
                        Long existingPriceItemId = existingPriceItemDto.getExistingPriceItemId();

                        existingPriceItemDto.setPurposeObj(mapOptionalToOptionDto(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toPurposeOptionDto));
                        existingPriceItemDto.setCategoryObj(mapOptionalToOptionDto(requestItemCategoryRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
                        existingPriceItemDto.setSubCategoryObj(mapOptionalToOptionDto(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
                        existingPriceItemDto.setCurrencyObj(mapOptionalToOptionDto(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemId), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));

                        if(existingPriceItemDto.getExistingPriceItemSupplierList() != null && existingPriceItemDto.getExistingPriceItemSupplierList().size() == 1) {
                            OptionDto supplierObj = existingPriceItemDto.getExistingPriceItemSupplierList().get(0).getSupplierObj();
                            existingPriceItemDto.setSupplierObj(supplierObj);
                        }

                        LocationDto locationDto = requestItemLocation != null ? locationService.getLocationById(requestItemLocation.getLocation().getRecId()) : null;
                        if(locationDto != null) {
                            existingPriceItemDto.setDeliveryLocation(locationDto);
                            existingPriceItemDto.setLocation(locationDto.getAddress());
                            existingPriceItemDto.setContactName(locationDto.getContactName());
                            existingPriceItemDto.setContactPhone(locationDto.getPhone());
                        }

                        return existingPriceItemDto;
                    }).collect(Collectors.toList());
        }
        return existingPriceItemList;
    }

    @Override
    public ExistingPriceItemResponseDto getExistingPriceItemByRequestIdAndRequestItemId(Long requestId, Long requestItemId, Integer tenantId, String sourcingDocNo) {
        ExistingPriceItem existingPriceItem = existingPriceItemRepository.getExistingPriceItemByRequestIdAndRequestItemIdAndNullableSourcingDocNo(requestId, requestItemId, tenantId, sourcingDocNo);
        Map<String,UserDetailResponse> userDetailMap = new HashMap<>();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), userDetailMap);
        return ExistingPriceItemMapper.INSTANCE.toExistingPriceItemResponseDto(existingPriceItem, timeZone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SourcingItemSearchDto updateItemStatusByRequest(
            Request request,
            Pageable pageable,
            String authCode,
            String pathUrl,
            Integer sourcingTypeId,
            String sourcingDocNo) {

        Map<String, UserDetailResponse> userDetailMap = new HashMap<>();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), userDetailMap);
        List<SourcingItemDto> sourcingItemList = new ArrayList<>();
        List<RequestItem> requestItemList = new ArrayList<>();
        Page<RequestItem> requestItemPage = null;

        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();
        ExcSourcing excSourcing = null;
        // -----------------------------------------------
        // ✅ 1. เลือก source ของข้อมูล RequestItem ตาม sourcingDocNo
        // -----------------------------------------------
        if (StringUtils.isNotBlank(sourcingDocNo)) {

            if (sourcingTypeId != null && StringUtils.isNotBlank(sourcingDocNo)) {
                excSourcing = excSourcingRepository.findByExcSourcingDocNo(sourcingDocNo).orElse(null);
            }

            // 🔁 เปลี่ยนเป็น: ดึงจาก ExistingPriceItem ก่อนเสมอ
            List<ExistingPriceItem> existingPriceItems = (sourcingTypeId != null)
                    ? existingPriceItemRepository.findByRequestAndSourcingDocNoAndSourcingTypeId(request, sourcingDocNo, sourcingTypeId)
                    : existingPriceItemRepository.findByRequestAndSourcingDocNo(request, sourcingDocNo);

            if (existingPriceItems != null && !existingPriceItems.isEmpty()) {
                // ได้ครบทุก item แม้เคลียร์ docNo ใน RequestItem ไปแล้ว
                requestItemList = existingPriceItems.stream()
                        .map(ExistingPriceItem::getRequestItem)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());

                // ในเส้นทางนี้ไม่ได้ใช้ page จาก DB -> ให้ตั้งค่าหน้าเองด้านล่างด้วย size ของ list
                requestItemPage = null; // บอกให้ช่วงคำนวณ total/page ใช้ list.size()
            } else {
                // ถ้าไม่มีใน ExistingPriceItem (เช่น ยังไม่เคยสร้างเอกสาร) ค่อย fallback ไป RequestItem
                if (sourcingTypeId != null) {
                    requestItemPage = requestItemRepository
                            .findByRequestAndSourcingDocNoAndSourcingTypeId(request.getRecId(), sourcingDocNo, sourcingTypeId, pageable);
                } else {
                    requestItemPage = requestItemRepository
                            .findByRequestAndSourcingDocNo(request, sourcingDocNo, pageable);
                }
                requestItemList = (requestItemPage != null) ? requestItemPage.getContent() : Collections.emptyList();
            }

        } else {
            // ไม่มี sourcingDocNo → ใช้ logic เดิม
            requestItemPage = requestItemRepository.findByRequest(request, pageable);
            requestItemList = (requestItemPage != null) ? requestItemPage.getContent() : Collections.emptyList();
        }


        // -----------------------------------------------
        // ✅ 2. เริ่ม logic เดิม (คงไว้ทั้งหมด)
        // -----------------------------------------------
        SourcingItemSearchDto sourcingItemSearchDto = new SourcingItemSearchDto();
        if (requestItemList != null && !requestItemList.isEmpty()) {

            DelegationDto delegationDto = null;
            DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
            delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
            delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

            boolean isDelegationActive = false;
            DelegationActiveResponse delegationActiveResponse =
                    delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
            if (delegationActiveResponse.getData() != null) {
                delegationDto = delegationActiveResponse.getData();
                isDelegationActive = true;
            }

            // ใช้ page info จากที่หาได้จริง (หรือ default)
            int totalPage = (requestItemPage != null) ? requestItemPage.getTotalPages() : 1;
            long total = (requestItemPage != null) ? requestItemPage.getTotalElements() : requestItemList.size();

            // -----------------------------------------------
            // ✅ 3. map RequestItem -> SourcingItemDto (คง logic เดิม)
            // -----------------------------------------------
            List<String> sourcingDocNos = requestItemList.stream()
                    .filter(r -> r.getSourcingType() != null
                            && r.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id())
                            && r.getSourcingDocNo() != null)
                    .map(RequestItem::getSourcingDocNo)
                    .distinct()
                    .collect(Collectors.toList());

            List<Long> eRFXDocNums = sourcingDocNos.stream().map(Long::parseLong).collect(Collectors.toList());
            ERFXStatusRequest eRFXStatusRequest = new ERFXStatusRequest();
            eRFXStatusRequest.setErfxNum(eRFXDocNums);

            for (RequestItem requestItem : requestItemList) {
                SourcingItemDto sourcingItemDto = sourcingItemDetail(
                        requestItem, authCode, timeZone, pathUrl, userDetailMap, delegationDto, excSourcing);
                sourcingItemList.add(sourcingItemDto);
            }

            requestItemRepository.saveAll(requestItemList);
            requestStatusService.updateRequestAndApprovalStatus(requestItemList, request);

            sourcingItemList.sort(Comparator.comparing(SourcingItemDto::getRequestItemId));
            sourcingItemSearchDto = new SourcingItemSearchDto();

            request = requestRepository.findRequestByRecId(request.getRecId());
            RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request, timeZone);
            requestDto.setEnableForwardButton(!isDelegationActive);

            boolean allowUpdateRequest = (AppUtil.getPrivilegeScopes().get("SQN") != null ||
                    AppUtil.getPrivilegeScopes().get("SQP") != null) && !isDelegationActive;
            boolean allowCopyToPR = AppUtil.getPrivilegeScopes().get("SQR") != null;

            RequestStatusDto requestStatusByPrivilegeCode =
                    updateRequestStatusByPrivilegeCode(requestDto.getRequestStatus(), allowUpdateRequest, allowCopyToPR);
            if (requestStatusByPrivilegeCode != null) {
                requestDto.setRequestStatus(requestStatusByPrivilegeCode);
            }

            ApprovalStatusDto approvalStatusByPrivilegeCode =
                    updateApprovalStatusByPrivilegeCode(requestDto.getApprovalStatus(), allowUpdateRequest, allowCopyToPR);
            if (approvalStatusByPrivilegeCode != null) {
                requestDto.setApprovalStatus(approvalStatusByPrivilegeCode);
            }

            sourcingItemSearchDto.setApprovalStatus(requestDto.getApprovalStatus());
            sourcingItemSearchDto.getApprovalStatus().setCanAssignToMe(
                    request.getAssignedBy() == null &&
                            request.getApprovalStatus().getName() == TENANT_REQUEST_AWAITING.code());
            sourcingItemSearchDto.setRequestStatus(requestDto.getRequestStatus());
            sourcingItemSearchDto.setSourcingItemDtoList(sourcingItemList);
            sourcingItemSearchDto.setApproverHeaders(this.getApproverHeaders(tenantId, idp, request));

            sourcingItemSearchDto.setTotal(total);
            sourcingItemSearchDto.setTotalPage(totalPage);
            sourcingItemSearchDto.setPageSize(pageable.getPageSize());
            sourcingItemSearchDto.setPage(pageable.getPageNumber() + 1);
        }

        return sourcingItemSearchDto;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getERFXSourcingStatus(List<ERFXStatusDto> eRFXStatusDtos, RequestItem requestItem) {

        if (eRFXStatusDtos.size() > 0) {
            Optional<ERFXStatusDto> filteredERFXStatusDto = eRFXStatusDtos.stream().filter(e -> e.getErfxNum() == Long.parseLong(requestItem.getSourcingDocNo())).findFirst();//.collect(Collectors.toList());
            if (filteredERFXStatusDto != null && filteredERFXStatusDto.isPresent()) {
                String eRFXSourcingStatusCode = filteredERFXStatusDto.get().getStatus();
                SourcingStatus eRFXSourcingStatus = sourcingStatusRepository.findSourcingStatusByCode(eRFXSourcingStatusCode);
                if (eRFXSourcingStatus != null) {
                    if(eRFXSourcingStatus.getRecId() != requestItem.getSourcingStatus().getRecId()) {
                        System.out.println("Affected RequestItem :" + requestItem.getRecId());
                        // In case User perform Cancel Shortlist
                        if(eRFXSourcingStatus.getRecId() == SOURCING_AWAITING_SHORTLIST.id()) {
                            if(requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_SHORTLIST.id() ||
                                    requestItem.getSourcingStatus().getRecId() == SOURCING_QUALIFIED_SUPPLIER.id() ||
                                    requestItem.getSourcingStatus().getRecId() == SOURCING_NO_QUALIFIED_SUPPLIER.id() ||
                                    requestItem.getSourcingStatus().getRecId() == SOURCING_NO_SUPPLIER_RESPONSE.id() ||
                                    requestItem.getSourcingStatus().getRecId() == SOURCING_NO_SUPPLIER_SELECTED.id()) {

                                //Permanent Delete all eRFX Attachments relevant to this RequestItem
                                if(requestItem.getRequestItemAttachmentList() != null && !requestItem.getRequestItemAttachmentList().isEmpty()) {
                                    for(RequestItemAttachment requestItemAttachment : requestItem.getRequestItemAttachmentList()) {
                                        if(requestItemAttachment.getAttachment().getFileGroup().equalsIgnoreCase("REQIERFX") ) {
                                            requestItemAttachmentRepository.deleteRequestItemAttachmentByRequestItemIdAndAttachmentId(requestItem.getRecId(), requestItemAttachment.getAttachment().getRecId());
//                                            requestItemAttachmentService.deleteByRequestItemAndAttachment(requestItem.getRecId(), requestItemAttachment.getAttachment().getRecId());
//                                            attachmentService.deleteAttachment(requestItem.getTenant(), requestItemAttachment.getAttachment().getRecId());
                                        }
                                    }
                                }
                            }
                        }
                        if ((eRFXSourcingStatus.getRecId() == SOURCING_QUALIFIED_SUPPLIER.id() || eRFXSourcingStatus.getRecId() == SOURCING_NO_QUALIFIED_SUPPLIER.id())
                                && (requestItem.getSourcingStatus().getRecId() == SOURCING_NO_SUPPLIER_RESPONSE.id() || requestItem.getSourcingStatus().getRecId() == SOURCING_NO_SUPPLIER_SELECTED.id())
                        ) {
                            System.out.println("Not Update RequestItem Status NO_SUPPLIER_RESPONSE and NO_SUPPLIER_SELECTED :" + requestItem.getRecId());
                        } else {
                            requestItem.setSourcingStatus(eRFXSourcingStatus);
//                            requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        }
                    }
                }
                requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            }
        }
    }

    @Override
    public SourcingItemDto sourcingItemDetail(RequestItem requestItem, String authCode, String timeZone, String pathUrl, Map<String,UserDetailResponse> userDetailMap, DelegationDto delegationDto, ExcSourcing excSourcing) {
        Long requestId = requestItem.getRequest().getRecId();
        Long requestItemId = requestItem.getRecId();
        Integer tenantId = requestItem.getTenant().getRecId();
        String sourcingDocNoVal = null;
        Integer sourcingTypeIdVal = null;
        SourcingStatusDto sourcingStatusVal = null;


        if(excSourcing != null) {
            sourcingDocNoVal = excSourcing.getExcSourcingDocNo();
            sourcingTypeIdVal = SOURCING_TYPE_EXCEPTIONAL_SOURCING.id();
            sourcingStatusVal = TenantExcSourcingStatusMapper.INSTANCE.toSourcingStatusDto(excSourcing.getTenantExcSourcingStatus());
        } else {
            sourcingDocNoVal = requestItem.getSourcingDocNo();
            sourcingTypeIdVal = requestItem.getSourcingType().getRecId();
            sourcingStatusVal = this.evaluateSourcingStatus(requestItem, authCode, pathUrl, userDetailMap, delegationDto);
        }

        ExistingPriceItem existingPriceItem = existingPriceItemRepository.getExistingPriceItemByRequestIdAndRequestItemIdAndNullableSourcingDocNo(requestId, requestItemId, tenantId, sourcingDocNoVal);
        Request request = requestRepository.findRequestByRecId(requestItem.getRequest().getRecId());

        SourcingItemDto sourcingItemDto = new SourcingItemDto();
        sourcingItemDto.setRequestId(requestItem.getRequest().getRecId());
        sourcingItemDto.setRequestItemId(requestItem.getRecId());
        sourcingItemDto.setSourcingDocNo(sourcingDocNoVal);
        sourcingItemDto.setSourcingDocId(requestItem.getSourcingDocId());
        sourcingItemDto.setSourcingTypeId(sourcingTypeIdVal);
        sourcingItemDto.setSourcingTypeName(requestItem.getSourcingType().getName());
        sourcingItemDto.setQuantity(requestItem.getQuantity());
        sourcingItemDto.setConditions(requestItem.getConditions());
        sourcingItemDto.setSourcingStatus(sourcingStatusVal);
        sourcingItemDto.setCancellationReason(requestItem.getCancellationReason());
        sourcingItemDto.setDeletionReason(requestItem.getDeletionReason());
        sourcingItemDto.setItemBudget(requestItem.getItemBudget());

        List<RequestItemAttachment> requestItemAttachmentList = requestItemAttachmentRepository.findRequestItemAttachmentsByRequestItem(requestItem);
        if (requestItemAttachmentList != null && !requestItemAttachmentList.isEmpty()) {

            List<RequestItemAttachmentDto> requestItemAttachmentDtoList = RequestItemAttachmentMapper.INSTANCE.toRequestItemAttachmentDtoList(requestItemAttachmentList);
            requestItemAttachmentDtoList.stream()
                    .map(requestItemAttachmentDto -> {
                        if(requestItemAttachmentDto.getAttachment().getFileURL() != null) {
                            String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(),  requestItemAttachmentDto.getAttachment().getFileURL());
                            try {
                                if(requestItemAttachmentDto.getAttachment().getFileSize() == null) {
                                    requestItemAttachmentDto.getAttachment().setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            requestItemAttachmentDto.getAttachment().setFileURL(fullyFileURL);
                        }
                        requestItemAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(requestItemAttachmentDto.getAttachment().getCreatedBy()));
                        return requestItemAttachmentDto;
                    }).collect(Collectors.toList());

            sourcingItemDto.setRequestItemAttachmentList(requestItemAttachmentDtoList);
        }

        if (existingPriceItem != null && existingPriceItem.getRecId() != null) {

            ExistingPriceItemSupplier existingPriceItemSupplier = null;

            if (existingPriceItem.getExistingPriceItemSupplierList() != null && existingPriceItem.getExistingPriceItemSupplierList().size() == 1) {
                existingPriceItemSupplier = existingPriceItem.getExistingPriceItemSupplierList().get(0);
            }
            boolean isShowSupplierLocalLanguage = tenantConfigService.isShowSupplierLocalLanguage(tenantId);
            String supplierFieldName = tenantConfigService.getSupplierFieldName(tenantId);
            if (sourcingTypeIdVal == SOURCING_TYPE_EXISTING_PRICE.id() || sourcingTypeIdVal == SOURCING_TYPE_EXCEPTIONAL_SOURCING.id() ||
                    (sourcingTypeIdVal == SOURCING_TYPE_ERFX.id()
                            && requestItem.getSourcingStatus().getRecId() == SOURCING_QUALIFIED_SUPPLIER.id())) {

                if (sourcingTypeIdVal == SOURCING_TYPE_EXISTING_PRICE.id() || sourcingTypeIdVal == SOURCING_TYPE_EXCEPTIONAL_SOURCING.id()) {
                    sourcingItemDto.setExistingPriceItemUnitId(existingPriceItem.getUnit().getRecId());
                    sourcingItemDto.setExistingPriceItemUnitCode(existingPriceItem.getUnit().getCode());
                }

                if (existingPriceItemSupplier != null) {


                    sourcingItemDto.setExistingPriceItemUnitId(existingPriceItem.getUnit().getRecId());
                    sourcingItemDto.setExistingPriceItemUnitCode(existingPriceItem.getUnit().getCode());

                    //Supplier supplier = existingPriceItemSupplier.getSupplier();
                    sourcingItemDto.setSupplierId(existingPriceItemSupplier.getSupplier().getRecId());
                    sourcingItemDto.setSupplierName(existingPriceItemSupplier.getSupplierFullName()); //SupplierNameUtil.resolveSupplierName(supplier, isShowSupplierLocalLanguage, supplierFieldName));
                }
                sourcingItemDto.setExistingPriceItemName(existingPriceItem.getItemName());
                sourcingItemDto.setExistingPriceItemDescription(existingPriceItem.getItemDescription());
                if (existingPriceItem.getExistingPriceItemSupplierList() != null && existingPriceItem.getExistingPriceItemSupplierList().size() == 1) {
                    BigDecimal unitPrice = existingPriceItem.getExistingPriceItemSupplierList().get(0).getUnitPrice();
                    sourcingItemDto.setUnitPrice(unitPrice);
                } else {
                    sourcingItemDto.setUnitPrice(existingPriceItem.getUnitPrice());
                }
                sourcingItemDto.setQuantity(existingPriceItem.getQuantity());
                sourcingItemDto.setConditions(existingPriceItem.getConditions());
                sourcingItemDto.setPartNo(existingPriceItem.getPartNo());
                sourcingItemDto.setBrand(existingPriceItem.getBrand());
                sourcingItemDto.setComment(existingPriceItem.getComment());
                sourcingItemDto.setVatTypeObj(OptionDtoMapper.INSTANCE.toVatTypeOptionDto(existingPriceItem.getVatTypeId()));
                sourcingItemDto.setCurrencyObj(OptionDtoMapper.INSTANCE.toCurrencyOptionDto(existingPriceItem.getCurrency()));
            }
            if (existingPriceItemSupplier != null
                    && (existingPriceItemSupplier.getUnitPrice() != null && existingPriceItemSupplier.getUnitPrice().doubleValue() == 0)
                    && requestItem.getSourcingStatus().getRecId() != SOURCING_AWAITING_SHORTLIST.id()
                    && sourcingTypeIdVal.equals(SOURCING_TYPE_ERFX.id())) {

                //Supplier supplier = existingPriceItemSupplier.getSupplier();

                sourcingItemDto.setFreeItem(true);
                sourcingItemDto.setExistingPriceItemName(null);
                sourcingItemDto.setExistingPriceItemDescription(null);
                sourcingItemDto.setExistingPriceItemUnitId(null);
                sourcingItemDto.setExistingPriceItemUnitCode(null);
                sourcingItemDto.setSupplierName(existingPriceItemSupplier.getSupplierFullName());//SupplierNameUtil.resolveSupplierName(supplier, isShowSupplierLocalLanguage, supplierFieldName));
            }
            else {
                sourcingItemDto.setFreeItem(false);
            }

            if (existingPriceItem.getExistingPriceItemSupplierList() != null && !existingPriceItem.getExistingPriceItemSupplierList().isEmpty()) {
                int requestType = request.getRequestTypeId();
                sourcingItemDto.setExistingPriceItemSupplierList(ExistingPriceItemSupplierMapper.INSTANCE.toExistingPriceItemSupplierDtoList(requestType, requestItem, existingPriceItem, existingPriceItem.getExistingPriceItemSupplierList(), timeZone));
            }
        }

        sourcingItemDto.setCreatedBy(requestItem.getCreatedBy());
        sourcingItemDto.setCreatedDate(requestItem.getCreatedDate());
        sourcingItemDto.setUpdatedBy(requestItem.getUpdatedBy());
        sourcingItemDto.setUpdatedDate(requestItem.getUpdatedDate());
        sourcingItemDto.setCreatedByName(UserDetailServiceUtil.getFullName(sourcingItemDto.getCreatedBy()));
        sourcingItemDto.setUpdatedByName(sourcingItemDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(sourcingItemDto.getUpdatedBy()) : null);

        // In case Purchaser view their request, do not allow to edit request item.
        if (AppUtil.isPurchaser()) {
            long sourcingTypeId = sourcingTypeIdVal;
            long sourcingStatusId = sourcingItemDto.getSourcingStatus().getRecId();

            boolean isExceptionalPending =
                    sourcingTypeId == SOURCING_TYPE_EXCEPTIONAL_SOURCING.id()
                            && sourcingStatusId == SOURCING_PENDING.id();

            boolean isNotExistingOrExceptional =
                    sourcingTypeId != SOURCING_TYPE_EXISTING_PRICE.id()
                            && sourcingTypeId != SOURCING_TYPE_EXCEPTIONAL_SOURCING.id();

            if (isExceptionalPending || isNotExistingOrExceptional) {
                sourcingItemDto.getSourcingStatus().setCanEdit(false);
            }
        }

        UserDetailResponse assignedByUserDetail;
        if(userDetailMap.containsKey(request.getAssignedBy())) {
            assignedByUserDetail = userDetailMap.get(request.getAssignedBy());
        } else {
            String userDetailKey = String.format("%s-%s", tenantId, request.getAssignedBy());
            assignedByUserDetail = uaaService.getUserDetailByTenantIdAndIdpAndUserName(
                    AppUtil.getTenantId(),
                    AppUtil.getIdp(),
                    userDetailKey,
                    request.getAssignedBy(),
                    userDetailMap);

            if(assignedByUserDetail != null) {
                if(!userDetailMap.containsKey(assignedByUserDetail.getUsername()))
                    userDetailMap.put(assignedByUserDetail.getUsername(), assignedByUserDetail);
            }
        }
        String firstName = assignedByUserDetail != null ? assignedByUserDetail.getFirstName() : "";
        String lastName = assignedByUserDetail != null ? assignedByUserDetail.getLastName() : "";
        String purchaserName =
                (firstName != null ? firstName : "") +
                        " " +
                        (lastName != null ? lastName : "");
        sourcingItemDto.setPurchaserName(purchaserName.trim());


        return SourcingItemMapper.INSTANCE.convertTimeStampByTimeZone(sourcingItemDto, timeZone);
    }

    @Override
    public SourcingRequestVisibleConfig getExistingPriceOptionConfig() {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        boolean visibled = tenantConfigService.getExistingPriceOptionConfig(tenant.getRecId());

        SourcingRequestVisibleConfig sourcingRequestVisibleConfig = new SourcingRequestVisibleConfig();
        sourcingRequestVisibleConfig.setTenantId(tenantCode);
        sourcingRequestVisibleConfig.setVisibled(visibled);

        return sourcingRequestVisibleConfig;
    }

    @Override
    public boolean rejectExistingPriceItem(Integer tenantId, DeleteExistingPriceItemRequest request) {
        boolean isReject = false;
        ExistingPriceItem existingPriceItem = existingPriceItemRepository.findExistingPriceItemByRecId(request.getExistingPriceItemId());
        if (existingPriceItem != null) {
            RequestItem requestItem = requestItemRepository.findRequestItemByRecId(existingPriceItem.getRequestItem().getRecId());
            SourcingStatus sourcingStatus = sourcingStatusRepository.findSourcingStatusByRecId(requestItem.getSourcingStatus().getRecId());
            SourcingStatus rejectStatus = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_REJECTED.id());

            if (sourcingStatus.isCanReject()) {
                requestItem.setDeletionReason(StringUtils.isNotBlank(request.getDeletionReason()) ? request.getDeletionReason() : null);
                requestItem.setSourcingStatus(rejectStatus);
                requestItem.setUpdatedBy(AppUtil.getUserName());
                requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                requestItemRepository.save(requestItem);

                isReject = true;
            }
        }
        return isReject;
    }

    private RequestStatusDto updateRequestStatusByPrivilegeCode(RequestStatusDto requestStatusDto, boolean allowUpdateRequest, boolean allowCopyToPR) {
        if(requestStatusDto != null) {
            requestStatusDto.setCanEdit(requestStatusDto.isCanEdit() & allowUpdateRequest);
            requestStatusDto.setCanCancel(requestStatusDto.isCanCancel() & allowUpdateRequest);
            requestStatusDto.setCanDelete(requestStatusDto.isCanDelete() & allowUpdateRequest);
            requestStatusDto.setCanDuplicate(requestStatusDto.isCanDuplicate() & allowUpdateRequest);
            requestStatusDto.setCanCopyToPR(requestStatusDto.isCanCopyToPR() & allowCopyToPR);
            return requestStatusDto;
        }
        return null;
    }

    private ApprovalStatusDto updateApprovalStatusByPrivilegeCode(ApprovalStatusDto approvalStatusDto, boolean allowUpdateRequest, boolean allowCopyToPR) {
        if(approvalStatusDto != null) {
            approvalStatusDto.setCanEdit(approvalStatusDto.isCanEdit() & allowUpdateRequest);
            approvalStatusDto.setCanCancel(approvalStatusDto.isCanCancel() & allowUpdateRequest);
            approvalStatusDto.setCanDelete(approvalStatusDto.isCanDelete() & allowUpdateRequest);
            approvalStatusDto.setCanDuplicate(approvalStatusDto.isCanDuplicate() & allowUpdateRequest);
            approvalStatusDto.setCanCopyToPR(approvalStatusDto.isCanCopyToPR() & allowCopyToPR);
            return approvalStatusDto;
        }
        return null;
    }

    private List<InstanceApproverHeaderDto> getApproverHeaders(String tenantId, String idp, Request request) {
        List<InstanceApproverHeaderDto> approverHeaders = new ArrayList<>();
        List<InstanceApproverSectionDto> approverSections = new ArrayList<>();
        InstanceApproverSectionDto approverSection = new InstanceApproverSectionDto();

        approverSection.setSectionName("Purchaser");
        approverSection.setNumberOfApproverRequired(1);
        List<InstanceApproverDto> instanceApprovers = this.getApprovers(tenantId, idp, request);

        boolean isApproved = instanceApprovers.stream()
                .anyMatch(item -> item.getStatus().equalsIgnoreCase("APPROVED"));

        boolean isRejected = instanceApprovers.stream()
                .anyMatch(item -> item.getStatus().equalsIgnoreCase("REJECTED"));

        boolean isCancelled = !isRejected & instanceApprovers.stream()
                .anyMatch(item -> item.getStatus().equalsIgnoreCase("CANCELLED"));

        if(isApproved) {
            instanceApprovers = instanceApprovers.stream()
                    .filter(item -> item.getStatus().equalsIgnoreCase("APPROVED"))
                    .collect(Collectors.toList());
        } else if(isRejected) {
            instanceApprovers = instanceApprovers.stream()
                    .filter(item -> item.getStatus().equalsIgnoreCase("REJECTED"))
                    .collect(Collectors.toList());
        } else if(isCancelled) {
            instanceApprovers = instanceApprovers.stream()
                    .filter(item -> item.getStatus().equalsIgnoreCase("CANCELLED"))
                    .collect(Collectors.toList());
        }


        approverSection.setApprovers(instanceApprovers);

        InstanceApproverDto instanceApproverDto = instanceApprovers.stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("APPROVED") ||
                        item.getStatus().equalsIgnoreCase("REJECTED") ||
                        item.getStatus().equalsIgnoreCase("CANCELLED") ||
                        item.getStatus().equalsIgnoreCase("FORWARDED"))
                .findFirst().orElse(null);

        InstanceApproverDto instanceAwaitingApproverDto = instanceApprovers.stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("AWAITING"))
                .findFirst().orElse(null);

        if(instanceApproverDto != null) {
            approverSection.setStatus(instanceApproverDto.getStatus());
        } else if(instanceAwaitingApproverDto != null){
            approverSection.setStatus("AWAITING");
        } else {
            approverSection.setStatus("PENDING");
        }
        approverSections.add(approverSection);
        InstanceApproverHeaderDto approverHeader = new InstanceApproverHeaderDto();
        approverHeader.setHeaderName("PURCHASER GROUP");
        approverHeader.setApproverSections(approverSections);
        approverHeaders.add(approverHeader);

        if(instanceApprovers.size() == 0)
            approverHeaders = null;

        if(approverHeaders != null) {
            List<InstanceApproverDto> approvers = approverSections.get(0).getApprovers();
            InstanceApproverDto AssignedPurchaser = approvers.get(0);
            InstanceApproverDto forwardedPurchaser = null;
            InstanceApproverDto delegatedPurchaser = null;

            boolean canForwardApprovalWorkflow = requestForwarderService.isCurrentForwarder(request.getRecId(), request.getAssignedBy());
            if(canForwardApprovalWorkflow) {
                ForwardedApprover forwardedApprover = requestForwarderService.getForwardedApprover(request.getRecId());
                log.info("Forward Approver : {}", forwardedApprover);
                ContractDetailClientDto contractDetail = uaaService.getContractDetail(tenantId, idp, forwardedApprover.getForwardedApprover(), new HashMap<>());


                if (contractDetail != null) {
                    forwardedPurchaser = new InstanceApproverDto();
                    forwardedPurchaser.setSysUserId(Integer.parseInt(contractDetail.getUserId()));
                    forwardedPurchaser.setLoginId(contractDetail.getUsername());
                    forwardedPurchaser.setFullName(String.format("%s %s", contractDetail.getFirstName(), contractDetail.getLastName()));
                    forwardedPurchaser.setEmail(contractDetail.getEmail());
                    forwardedPurchaser.setMobilePhone(contractDetail.getMobilePhone());
                    forwardedPurchaser.setPhone(contractDetail.getPhone());
                    // Set lasted status
                    forwardedPurchaser.setStatus(!approverSections.get(0).getStatus().isEmpty()
                            ? approverSections.get(0).getStatus()
                            : "AWAITING");
                    forwardedPurchaser.setRequired(true);
                    forwardedPurchaser.setComment(AssignedPurchaser.getComment());
//                    forwardedPurchaser.setDelegatedDate(forwardedApprover.getForwaredDate());
//                    forwardedPurchaser.setDelegatedBy(forwardedApprover.getFromApprover());
                    approvers.add(forwardedPurchaser);
                    AssignedPurchaser.setComment(null);
                }
                AssignedPurchaser.setStatus("FORWARED");
                AssignedPurchaser.setForwardedDate(forwardedApprover.getForwaredDate());

                // CASE FORWARDED WITH DELEGATED
                if (request.getAssignedBy() != null && request.getDelegateActionBy() != null && !request.getAssignedBy().equalsIgnoreCase(request.getDelegateActionBy())) {
                    ContractDetailClientDto contractDetailDelegatee = uaaService.getContractDetail(tenantId, idp, request.getDelegateActionBy(), new HashMap<>());
                    ContractDetailClientDto contractDetailDelegator = uaaService.getContractDetail(tenantId, idp, request.getAssignedBy(), new HashMap<>());

                    if (contractDetailDelegatee != null) {
                        delegatedPurchaser = new InstanceApproverDto();
                        delegatedPurchaser.setSysUserId(Integer.parseInt(contractDetailDelegatee.getUserId()));
                        delegatedPurchaser.setLoginId(contractDetailDelegatee.getUsername());
                        delegatedPurchaser.setFullName(String.format("%s %s", contractDetailDelegatee.getFirstName(), contractDetailDelegatee.getLastName()));
                        delegatedPurchaser.setEmail(contractDetailDelegatee.getEmail());
                        delegatedPurchaser.setMobilePhone(contractDetailDelegatee.getMobilePhone());
                        delegatedPurchaser.setPhone(contractDetailDelegatee.getPhone());

                        if(forwardedPurchaser != null) {
                            delegatedPurchaser.setStatus(forwardedPurchaser.getStatus());
                            delegatedPurchaser.setComment(forwardedPurchaser.getComment());
                        } else {
                            delegatedPurchaser.setStatus(AssignedPurchaser.getStatus());
                            delegatedPurchaser.setComment(AssignedPurchaser.getComment());
                        }
                        delegatedPurchaser.setRequired(true);
                        delegatedPurchaser.setDelegatedBy(String.format("%s %s", contractDetailDelegator.getFirstName(), contractDetailDelegator.getLastName()));
                        approvers.add(delegatedPurchaser);
                    }
                    if(forwardedPurchaser != null) {
                        forwardedPurchaser.setStatus("DELEGATED");
                        forwardedPurchaser.setDelegatedDate(request.getDelegateActionDate());
                        forwardedPurchaser.setComment(null);
                    } else {
                        AssignedPurchaser.setStatus("DELEGATED");
                        AssignedPurchaser.setDelegatedDate(request.getDelegateActionDate());
                        AssignedPurchaser.setComment(null);
                    }

                }
            } else if (request.getAssignedBy() != null && request.getDelegateActionBy() != null && !request.getDelegateActionBy().equalsIgnoreCase(request.getAssignedBy())) {
                ContractDetailClientDto contractDetailDelegatee = uaaService.getContractDetail(tenantId, idp, request.getDelegateActionBy(), new HashMap<>());
                ContractDetailClientDto contractDetailDelegator = uaaService.getContractDetail(tenantId, idp, request.getAssignedBy(), new HashMap<>());

                if (contractDetailDelegatee != null) {
                    delegatedPurchaser = new InstanceApproverDto();
                    delegatedPurchaser.setSysUserId(Integer.parseInt(contractDetailDelegatee.getUserId()));
                    delegatedPurchaser.setLoginId(contractDetailDelegatee.getUsername());
                    delegatedPurchaser.setFullName(String.format("%s %s", contractDetailDelegatee.getFirstName(), contractDetailDelegatee.getLastName()));
                    delegatedPurchaser.setEmail(contractDetailDelegatee.getEmail());
                    delegatedPurchaser.setMobilePhone(contractDetailDelegatee.getMobilePhone());
                    delegatedPurchaser.setPhone(contractDetailDelegatee.getPhone());

                    if(forwardedPurchaser != null) {
                        delegatedPurchaser.setStatus(forwardedPurchaser.getStatus());
                        delegatedPurchaser.setComment(forwardedPurchaser.getComment());
                    } else {
                        delegatedPurchaser.setStatus(AssignedPurchaser.getStatus());
                        delegatedPurchaser.setComment(AssignedPurchaser.getComment());
                    }
                    delegatedPurchaser.setRequired(true);
                    delegatedPurchaser.setDelegatedBy(String.format("%s %s", contractDetailDelegator.getFirstName(), contractDetailDelegator.getLastName()));
                    approvers.add(delegatedPurchaser);
                }
                if(forwardedPurchaser != null) {
                    forwardedPurchaser.setStatus("DELEGATED");
                    forwardedPurchaser.setDelegatedDate(request.getDelegateActionDate());
                    forwardedPurchaser.setComment(null);
                } else {
                    AssignedPurchaser.setStatus("DELEGATED");
                    AssignedPurchaser.setDelegatedDate(request.getDelegateActionDate());
                    AssignedPurchaser.setComment(null);
                }
            }
        }

        return approverHeaders;
    }

    private List<InstanceApproverDto> getApprovers(String tenantId, String idp, Request request) {

        // Find instanceApproverId by ApproverName & DocumentId
        WorkflowInstanceApproverCriteria criteria = new WorkflowInstanceApproverCriteria();
        criteria.setPageNumber(1);
        criteria.setPageSize(100);

        List<String> refDocumentIds = new ArrayList<>();
        refDocumentIds.add(request.getRecId().toString());
        criteria.setRefDocumentId(refDocumentIds);

        List<String> statusList = new ArrayList<>();
        statusList.add("PENDING");
        statusList.add("AWAITING");
        statusList.add("APPROVED");
        statusList.add("REJECTED");
        statusList.add("CANCELLED");
        statusList.add("FORWARDED");
        criteria.setStatus(statusList);

        // Optional Params
        List<Long> workflowInstanceIds = new ArrayList<>();
        workflowInstanceIds.add(request.getWorkflowInstanceId());
        criteria.setWorkflowInstanceId(workflowInstanceIds);

        Page<WorkflowInstApproverDto> approvers = workflowInstanceApprovalService.getByCriteria(criteria);

        List<InstanceApproverDto> instanceApprovers = approvers.getContent().stream()
                .map(item -> {
                    Map<String,UserDetailResponse> userDetailMap = new HashMap<>();
                    ContractDetailClientDto contractDetail = uaaService.getContractDetail(tenantId, idp, item.getReferApproverId(), userDetailMap);
                    if(contractDetail != null) {
                        InstanceApproverDto approver = new InstanceApproverDto();
                        approver.setSysUserId(Integer.parseInt(contractDetail.getUserId()));
                        approver.setLoginId(contractDetail.getUsername());
                        approver.setFullName(String.format("%s %s", contractDetail.getFirstName(), contractDetail.getLastName()));
                        approver.setEmail(contractDetail.getEmail());
                        approver.setMobilePhone(contractDetail.getMobilePhone());
                        approver.setPhone(contractDetail.getPhone());
                        approver.setStatus(item.getStatus());
                        approver.setRequired(item.getIsRequired());
                        approver.setComment(item.getRemark());
                        return approver;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return instanceApprovers;
    }

    public Integer getSupplier(String TPShortName) {
        int supplierId = 0;
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantRepository.findTenantByCode(tenantCode);

        //get supplier web work
        List<TenantInfoResponse> response = uaaService.getTenantInfo(tenant.getCode());
        boolean isSearchByInvitationCode = tenantConfigService.getIsSearchByInvitationCode(tenant.getRecId());

        SupplierWebWorkSearchByTPShortNameRequest supplierWebWorkSearchByTPShortNameRequest = new SupplierWebWorkSearchByTPShortNameRequest();
        supplierWebWorkSearchByTPShortNameRequest.setTPShortName(TPShortName);
        supplierWebWorkSearchByTPShortNameRequest.setInvitationCode(isSearchByInvitationCode ? (response != null && !response.isEmpty() ? response.get(0).getInvitation_code() : AppUtil.getTenantId()) : "");
        SupplierWebworksDto supplierWebworksDto = supplierService.getSupplierWebWorkByTPShortName(supplierWebWorkSearchByTPShortNameRequest);

        if (supplierWebworksDto != null) {
            //supplier sourcing
            Optional<Supplier> supplierOptional = supplierRepository.getSupplierByTenantAndShortName(tenant, TPShortName);

            if(!supplierOptional.isPresent()) {
                Supplier supplier = new Supplier();
                supplier.setTenant(tenant);
                supplier.setShortName(supplierWebworksDto.getTPShortName());
                supplier.setFullCompanyNameLocal(supplierWebworksDto.getFullCompanyNameLocal());
                supplier.setFullCompanyNameEN(supplierWebworksDto.getFullCompanyNameEN());
                supplier.setCompanyNameLocal(supplierWebworksDto.getCompanyNameLocal());
                supplier.setCompanyNameEN(supplierWebworksDto.getCompanyNameEN());
                supplier.setBranchNameLocal(supplierWebworksDto.getBranchNameLocal());
                supplier.setBranchNameEN(supplierWebworksDto.getBranchNameEN());
                supplier.setTaxId(supplierWebworksDto.getTaxId());

                supplier.setCreatedBy(AppUtil.getUserName());
                supplier.setCreatedDate(DateTimeUtil.getTimestampUTC());
                supplierRepository.save(supplier);
            }

            supplierOptional = supplierRepository.getSupplierByTenantAndShortName(tenant, TPShortName);
            supplierId = supplierOptional.get().getRecId();
        } else {
            Optional<Supplier> supplierOptional = supplierRepository.getSupplierByTenantAndShortName(tenant, TPShortName);
            if (supplierOptional.isPresent()) {
                supplierId = supplierOptional.get().getRecId();
            }
        }
        return supplierId;
    }

    private ExistingPriceItem setExistingPriceItem(RequestItem requestItem, ExistingPriceItemRequest existingPriceItemRequest, boolean isSubmit) {
        ExistingPriceItem existingPriceItem = new ExistingPriceItem();

//        int supplierId = getSupplier(existingPriceItemRequest.getTpShortName());
        if (requestItem != null) {
            Request request = requestItem.getRequest();
            Tenant tenant = requestItem.getTenant();
            Currency currency = currencyRepository.findCurrenciesByRecId(Integer.parseInt(existingPriceItemRequest.getCurrencyObj().getValue()));
//            Supplier supplier = supplierRepository.findSupplierByRecId(supplierId);
            Unit unit = unitRepository.findByRecId(Integer.parseInt(existingPriceItemRequest.getUnitObj().getValue()));

            ExistingPriceItem existingPriceItemItemHeader = existingPriceItemRepository.findByRequestItemAndNullableSourcingDocNo(requestItem, requestItem.getSourcingDocNo());
            if (existingPriceItemItemHeader != null) {
                existingPriceItem = existingPriceItemItemHeader;
            } else {
                existingPriceItem.setCreatedBy(AppUtil.getUserName());
                existingPriceItem.setCreatedDate(DateTimeUtil.getTimestampUTC());
            }

            existingPriceItem.setRequest(request);
            existingPriceItem.setRequestItem(requestItem);
            existingPriceItem.setTenant(tenant);
            existingPriceItem.setMaterialCode(StringUtils.isNotBlank(existingPriceItemRequest.getMaterialCode()) ? existingPriceItemRequest.getMaterialCode() : null);
            existingPriceItem.setItemName(StringUtils.isNotBlank(existingPriceItemRequest.getItemName()) ? existingPriceItemRequest.getItemName() : null);
            existingPriceItem.setItemDescription(StringUtils.isNotBlank(existingPriceItemRequest.getItemDescription()) ? existingPriceItemRequest.getItemDescription() : null);
            existingPriceItem.setQuantity(existingPriceItemRequest.getQuantity());
            existingPriceItem.setConditions(existingPriceItemRequest.getConditions());
            existingPriceItem.setBrand(StringUtils.isNotBlank(existingPriceItemRequest.getBrand()) ? existingPriceItemRequest.getBrand() : null);
            existingPriceItem.setPartNo(StringUtils.isNotBlank(existingPriceItemRequest.getPartNo()) ? existingPriceItemRequest.getPartNo() : null);
//            existingPriceItem.setSupplier(supplier);
            existingPriceItem.setUnit(unit);
            existingPriceItem.setUnitPrice(existingPriceItemRequest.getUnitPrice());
            existingPriceItem.setCurrency(currency);
            existingPriceItem.setComment(StringUtils.isNotBlank(existingPriceItemRequest.getComment()) ? existingPriceItemRequest.getComment() : null);
            if(existingPriceItemRequest.getVatTypeObj() != null) {
                existingPriceItem.setVatTypeId(StringUtils.isNotBlank(existingPriceItemRequest.getVatTypeObj().getValue()) ? Integer.parseInt(existingPriceItemRequest.getVatTypeObj().getValue()) : null);
            }

            existingPriceItem.setSourcingTypeId(existingPriceItemRequest.getSourcingTypeId());
            String sourcingDocNo = null;
            if(StringUtils.isNotBlank(existingPriceItemRequest.getSourcingDocNo()) && !existingPriceItemRequest.getSourcingDocNo().isEmpty()){
                sourcingDocNo = existingPriceItemRequest.getSourcingDocNo();
            } else if (Objects.equals(existingPriceItemRequest.getSourcingTypeId(), SOURCING_TYPE_EXISTING_PRICE.id())) {
                sourcingDocNo = sourcingReferenceService.generateSourcingNumber("EXISTING_PRICE", tenant.getRecId(), existingPriceItemRequest.getOrganizationId());
            }
            existingPriceItem.setSourcingDocNo(sourcingDocNo);

            existingPriceItem.setUpdatedBy(AppUtil.getUserName());
            existingPriceItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());

        }
        return existingPriceItem;
    }

    private ExistingPriceItemDto existingPriceItemDetail(RequestItem requestItem, String authCode, String pathUrl, Map<String,UserDetailResponse> userDetailMap, DelegationDto delegationDto) {
        ExistingPriceItem existingPriceItem = existingPriceItemRepository.findByRequestItemAndNullableSourcingDocNo(requestItem, requestItem.getSourcingDocNo());
        Request request = requestRepository.findRequestByRecId(requestItem.getRequest().getRecId());
        List<RequestItemPr> requestItemPrList = requestItemPrRepository.findByRequestItem(requestItem);
        RequestItemLocation requestItemLocation = requestItemLocationRepository.findRequestItemLocationByRequestItemRecId(requestItem.getRecId());
        SourcingStatusDto sourcingStatusDto = this.evaluateSourcingStatus(requestItem, authCode, pathUrl, userDetailMap, delegationDto);

        UserDetailResponse assignedByUserDetail;
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        String userDetailKey = String.format("%s-%s", tenant.getRecId(), request.getAssignedBy());

        if (userDetailMap.containsKey(userDetailKey)) {
            assignedByUserDetail = userDetailMap.get(userDetailKey);
        } else {
            assignedByUserDetail = uaaService.getUserDetailByTenantIdAndIdpAndUserName(
                    AppUtil.getTenantId(),
                    AppUtil.getIdp(),
                    userDetailKey,
                    request.getAssignedBy(),
                    userDetailMap);
            if (assignedByUserDetail != null) {
                if(!userDetailMap.containsKey(assignedByUserDetail.getUsername()))
                    userDetailMap.put(assignedByUserDetail.getUsername(), assignedByUserDetail);
            }
        }

        String firstName = assignedByUserDetail != null ? assignedByUserDetail.getFirstName() : "";
        String lastName = assignedByUserDetail != null ? assignedByUserDetail.getLastName() : "";

        List<ExistingPriceItemAttachment> existingPriceItemAttachmentList = null;
        ExistingPriceItemSupplier existingExistingPriceItemSupplier = null;
        if (existingPriceItem != null) {
            ExistingPriceItem priceItem = existingPriceItemRepository.findExistingPriceItemByRecId(existingPriceItem.getRecId());
            existingPriceItemAttachmentList = existingPriceItemAttachmentRepository.findExistingPriceItemAttachmentsByExistingPriceItem(priceItem);
            if(existingPriceItem.getExistingPriceItemSupplierList() != null && existingPriceItem.getExistingPriceItemSupplierList().size() == 1) {
                existingExistingPriceItemSupplier = existingPriceItem.getExistingPriceItemSupplierList().get(0);
            }
        }

        Optional<RequestType> requestType = Optional.ofNullable(requestTypeRepository.findTop1ByRequestId(request.getRecId()).orElse(null));
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), userDetailMap);
        boolean isShowItemNameForFreeItem = tenantConfigService.IsShowItemNameForFreeItem(tenant.getRecId());
        boolean isShowSupplierLocalLanguage = tenantConfigService.isShowSupplierLocalLanguage(tenant.getRecId());
        String supplierFieldName = tenantConfigService.getSupplierFieldName(tenant.getRecId());

        return ExistingPriceItemMapper.INSTANCE.toExistingPriceItemDto(
                requestItem,
                existingPriceItem,
                request,
                requestType.map(RequestType::getType).orElse(null),
                requestItemPrList,
                requestItem.getRequestItemAttachmentList(),
                requestItemLocation,
                existingPriceItemAttachmentList,
                existingExistingPriceItemSupplier,
                sourcingStatusDto,
                firstName,
                lastName,
                timeZone,
                isShowItemNameForFreeItem,
                isShowSupplierLocalLanguage,
                supplierFieldName);
    }

    private ExistingPriceItemDtoV2 existingPriceItemDetailV2(RequestItem requestItem, String authCode, String pathUrl, Map<String,UserDetailResponse> userDetailMap, DelegationDto delegationDto, ExcSourcing excSourcing) {
        String sourcingDocNo = Optional.ofNullable(excSourcing)
                .map(ExcSourcing::getExcSourcingDocNo)
                .orElse(requestItem.getSourcingDocNo());
        ExistingPriceItem existingPriceItem = existingPriceItemRepository.findByRequestItemAndNullableSourcingDocNo(requestItem, sourcingDocNo);
        Request request = requestRepository.findRequestByRecId(requestItem.getRequest().getRecId());
        List<RequestItemPr> requestItemPrList = requestItemPrRepository.findByRequestItem(requestItem);
        RequestItemLocation requestItemLocation = requestItemLocationRepository.findRequestItemLocationByRequestItemRecId(requestItem.getRecId());
        SourcingStatusDto sourcingStatusDto = this.evaluateSourcingStatus(requestItem, authCode, pathUrl, userDetailMap, delegationDto);

        UserDetailResponse assignedByUserDetail;
        if(userDetailMap.containsKey(request.getAssignedBy())) {
            assignedByUserDetail = userDetailMap.get(request.getAssignedBy());
        } else {
            String tenantCode = AppUtil.getTenantId();
            Tenant tenant = tenantService.findByCode(tenantCode);
            String userDetailKey = String.format("%s-%s", tenant.getRecId(), request.getAssignedBy());
            assignedByUserDetail = uaaService.getUserDetailByTenantIdAndIdpAndUserName(
                    AppUtil.getTenantId(),
                    AppUtil.getIdp(),
                    userDetailKey,
                    request.getAssignedBy(),
                    userDetailMap);
            if(Objects.nonNull(assignedByUserDetail) && !userDetailMap.containsKey(assignedByUserDetail.getUsername())) {
                userDetailMap.put(assignedByUserDetail.getUsername(), assignedByUserDetail);
            }
        }

        String firstName = assignedByUserDetail != null ? assignedByUserDetail.getFirstName() : "";
        String lastName = assignedByUserDetail != null ? assignedByUserDetail.getLastName() : "";

        List<ExistingPriceItemAttachment> existingPriceItemAttachmentList = null;
        ExistingPriceItemSupplier existingExistingPriceItemSupplier = null;
        if (existingPriceItem != null) {
            ExistingPriceItem priceItem = existingPriceItemRepository.findExistingPriceItemByRecId(existingPriceItem.getRecId());
            existingPriceItemAttachmentList = existingPriceItemAttachmentRepository.findExistingPriceItemAttachmentsByExistingPriceItem(priceItem);
            if(existingPriceItem.getExistingPriceItemSupplierList() != null && existingPriceItem.getExistingPriceItemSupplierList().size() == 1) {
                existingExistingPriceItemSupplier = existingPriceItem.getExistingPriceItemSupplierList().get(0);
            }
        }

        Optional<RequestType> requestType = Optional.ofNullable(requestTypeRepository.findTop1ByRequestId(request.getRecId()).orElse(null));
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), userDetailMap);
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        boolean isShowItemNameForFreeItem = tenantConfigService.IsShowItemNameForFreeItem(tenant.getRecId());
        boolean isShowSupplierLocalLanguage = tenantConfigService.isShowSupplierLocalLanguage(tenant.getRecId());
        String supplierFieldName = tenantConfigService.getSupplierFieldName(tenant.getRecId());
        ExistingPriceItemDtoV2 existingPriceItemDto = ExistingPriceItemMapper.INSTANCE.toExistingPriceItemDtoV2(
                requestItem,
                existingPriceItem,
                request,
                requestType.map(RequestType::getType).orElse(null),
                requestItemPrList,
                requestItem.getRequestItemAttachmentList(),
                requestItemLocation,
                existingPriceItemAttachmentList,
                existingExistingPriceItemSupplier,
                sourcingStatusDto,
                firstName,
                lastName,
                timeZone,
                isShowItemNameForFreeItem,
                isShowSupplierLocalLanguage,
                supplierFieldName,
                excSourcing);

        existingPriceItemDto.setItemSequence(requestItem.getItemSequence());
        return existingPriceItemDto;
    }

    private String getUserRoleName(Request request, RequestItem requestItem, String pathUrl) {
        if(pathUrl.equalsIgnoreCase("sourcing-request")){
            return "Requester";
        } else if(pathUrl.equalsIgnoreCase("review-request")){
            return "Reviewer";
        }else if(pathUrl.equalsIgnoreCase("approve-request")){
            return "Approver";
        } else if(pathUrl.equalsIgnoreCase("manage-request")) {
            DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
            delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
            delegationActiveRequest.setDelegatorBy(request.getAssignedBy());
            Boolean isDelegationActive = false;

            DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
            if (delegationActiveResponse.getData() != null) {
                isDelegationActive = true;
            }

            if(request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) && request.getDelegateActionBy() == null) {
                return "PurchaserOwner";
            } else if(!request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) && request.getDelegateActionBy() == null && isDelegationActive) {
                return "PurchaserOwner";
            } else if(!request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) && request.getDelegateActionBy() != null && request.getDelegateActionBy().equalsIgnoreCase(AppUtil.getUserName())) {
                return "PurchaserOwner";
            } else if(request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) && request.getDelegateActionBy() != null && request.getDelegateActionBy().equalsIgnoreCase(AppUtil.getUserName())) {
                return "PurchaserOwner";
            } else if(!request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) && request.getDelegateActionBy() == null && !isDelegationActive) {
                return "PurchaserNotOwner";
            } else if(request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) && request.getDelegateActionBy() != null && !request.getDelegateActionBy().equalsIgnoreCase(AppUtil.getUserName())) {
                return "PurchaserNotOwner";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private String getPermissionByUserRoleName(String userRole, TenantSourcingStatusDto tenantSourcingStatusDto) {
        switch (userRole) {
            case "Requester":
                return tenantSourcingStatusDto.getRequester();
            case "Reviewer":
                return tenantSourcingStatusDto.getReviewer();
            case "Approver":
                return tenantSourcingStatusDto.getApprover();
            case "PurchaserNotOwner":
                return tenantSourcingStatusDto.getPurchaserNotOwner();
            case "PurchaserOwner":
                return tenantSourcingStatusDto.getPurchaserOwner();
            default: return "";
        }
    }

    private void evaluateSourcingStatusByPermission(String permission, SourcingStatusDto sourcingStatusDto, SourcingStatus sourcingStatus, String authCode, RequestItem requestItem) {
        switch (permission) {
            case "DISABLE_ERFX_NO": // Disable eRFX no. (Unclickable)
                sourcingStatusDto.setShowModalMsgInfo(false);
                sourcingStatusDto.setRedirectURL(null);
                break;
            case "ERFX_DRAFT": // Can see edit eRFX page
            case "ERFX_SHORTLIST": // Can see Shortlist page (Edit mode)
            case "ERFX_VIEW_DETAIL": // Can see Shortlist page (View mode)
                String redirectURL = this.generateERFXUrl(sourcingStatus.getServiceName(), authCode, requestItem.getSourcingDocNo());
                sourcingStatusDto.setShowModalMsgInfo(false);
                sourcingStatusDto.setRedirectURL(redirectURL);
                break;
            case "NO_PERMISSION": // Show users don't have access to this item
                sourcingStatusDto.setShowModalMsgInfo(true);
                sourcingStatusDto.setRedirectURL(null);
                break;
        }
    }

    private SourcingStatusDto evaluateSourcingStatus(RequestItem requestItem, String authCode, String pathUrl, Map<String,UserDetailResponse> userDetailMap, DelegationDto delegationDto) {

        //String redirectURL = "";
        Request request = requestRepository.findRequestByRecId(requestItem.getRequest().getRecId());
        SourcingStatus sourcingStatus = sourcingStatusRepository.findSourcingStatusByRecId(requestItem.getSourcingStatus().getRecId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), userDetailMap);
        SourcingStatusDto sourcingStatusDto = SourcingStatusMapper.INSTANCE.toSourcingStatusDto(sourcingStatus, timeZone);

        sourcingStatusDto.setShowModalMsgInfo(false);
        if (requestItem.getSourcingType().getRecId() == SOURCING_TYPE_DRAFT.id()) {
            sourcingStatusDto.setRedirectURL(null);
        } else if (requestItem.getSourcingType().getRecId() == SOURCING_TYPE_ERFX.id()) {

            sourcingStatusDto.setCanReject(false);
            sourcingStatusDto.setCanEdit(false);

            TenantSourcingStatusDto tenantSourcingStatusDto = tenantSourcingStatusService.getBySourcingStatusIdAndTenant(requestItem.getSourcingStatus().getRecId());
            String userRoleName = this.getUserRoleName(request, requestItem, pathUrl);
            String permission = this.getPermissionByUserRoleName(userRoleName, tenantSourcingStatusDto);
            evaluateSourcingStatusByPermission(permission, sourcingStatusDto, sourcingStatus, authCode, requestItem);


//            // Purchaser [Not Owner of request]  & Requester that create request
//            if (!request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) &&
//                    (request.getDelegateActionBy() == null || !request.getDelegateActionBy().equalsIgnoreCase(AppUtil.getUserName())) &&
//                    requestItem.getSourcingStatus().getRecId() == SOURCING_DRAFT.id()) {
//
//                sourcingStatusDto.setRedirectURL(null);
//                sourcingStatusDto.setShowModalMsgInfo(true);
//
//            } else if(request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) &&
//                    (request.getDelegateActionBy() != null && !request.getDelegateActionBy().equalsIgnoreCase(AppUtil.getUserName())) &&
//                    requestItem.getSourcingStatus().getRecId() == SOURCING_DRAFT.id()) {
//                sourcingStatusDto.setRedirectURL(null);
//                sourcingStatusDto.setShowModalMsgInfo(true);
//
//            } else if (((request.getCreatedBy().equalsIgnoreCase(AppUtil.getUserName()) && request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName())) ||
//                    (request.getDelegateActionBy() != null && request.getDelegateActionBy().equalsIgnoreCase(AppUtil.getUserName()))) &&
//                    requestItem.getSourcingStatus().getRecId() == SOURCING_DRAFT.id()) {
//
//                redirectURL = this.generateERFXUrl(sourcingStatus.getServiceName(), authCode, requestItem.getSourcingDocNo());
//                // Disable link for SOURCING_DRAFT status
//                if(pathUrl != null && pathUrl.equalsIgnoreCase("sourcing-request")) {
//                    sourcingStatusDto.setRedirectURL(null);
//                    sourcingStatusDto.setShowModalMsgInfo(true);
//                } else {
//                    sourcingStatusDto.setRedirectURL(redirectURL);
//                }
//
//            } else if (request.getCreatedBy().equalsIgnoreCase(AppUtil.getUserName()) &&
//                    !request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) &&
//                    requestItem.getSourcingStatus().getRecId() == SOURCING_DRAFT.id()) {
//
//                sourcingStatusDto.setRedirectURL(null);
//                sourcingStatusDto.setShowModalMsgInfo(true);
//
//            } else {
//
//                redirectURL = this.generateERFXUrl(sourcingStatus.getServiceName(), authCode, requestItem.getSourcingDocNo());
//                sourcingStatusDto.setRedirectURL(redirectURL);
//
//                List<RequestReviewerDto> requestReviewerDtos = requestReviewerService.findByRequest(request.getRecId());
//                Optional<RequestReviewerDto> requestReviewerDto = requestReviewerDtos.stream()
//                        .filter(requestReviewer -> requestReviewer.getReviewer().getLoginId().equalsIgnoreCase(AppUtil.getUserName())).findFirst();
//
//                List<RequestDeptApproverDto> requestDeptApproverDtos = requestDeptApproverService.findByRequest(request.getRecId());
//                Optional<RequestDeptApproverDto> requestDeptApproverDto = requestDeptApproverDtos.stream()
//                        .filter(requestDeptApprover -> requestDeptApprover.getApproverDto().getLoginId().equalsIgnoreCase(AppUtil.getUserName())).findFirst();
//
//                // --------------------------------------------------------------------------------------
//                // View Request => Consider pathUrl = "sourcing-request" and ignored PrivilegeCode
//                // Test Data > User : Verify / Pass : 12345
//                // --------------------------------------------------------------------------------------
//                // None                         >  Not show eRFX no.
//                // Draft                        >  Show user don't have access to this item
//                // Awaiting Response            >  Disable eRFX No. [Can’t Click]
//                // Pending                      >  Disable eRFX No. [Can’t Click]
//                // Cancelled                    >  Disable eRFX No. [Can’t Click]
//                // Awaiting Active              >  Disable eRFX No. [Can’t Click]
//                // Awaiting Shortlist           >  Disable eRFX No. [Can’t Click]
//                // Awaiting Approve Shortlist   >  Disable eRFX No. [Can’t Click]
//                // Deleted                      >  Not Show eRFX no.
//                // Rejected (SR Status)         >  Not Show eRFX no.
//                // Qualified Supplier           >  Shortlist
//                // No Qualified Supplier        >  Shortlist
//                // No Supplier Response         >  Shortlist
//                // No Supplier Selected         >  Shortlist
//
//                if (pathUrl != null && pathUrl.equalsIgnoreCase(PathUrl.SOURCING_REQUEST.path())) {
//                    if(requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_RESPONSE.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_PENDING.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_ACTIVE.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_SHORTLIST.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_APPROVE_SHORTLIST.id()
//                    ) {
//                        sourcingStatusDto.setRedirectURL(null);
//                    }
//                }
//
//                // --------------------------------------------------------------------------------------
//                // Reviewer
//                // Test Data > User : Kobchai / Pass : 12345
//                // --------------------------------------------------------------------------------------
//                // None                         >  Not show eRFX no.
//                // Draft                        >  Show user don't have access to this item
//                // Awaiting Response            >  Disable eRFX No. [Can’t Click]
//                // Pending                      >  Disable eRFX No. [Can’t Click]
//                // Cancelled                    >  Disable eRFX No. [Can’t Click]
//                // Awaiting Active              >  Disable eRFX No. [Can’t Click]
//                // Awaiting Shortlist           >  Disable eRFX No. [Can’t Click]
//                // Awaiting Approve Shortlist   >  Disable eRFX No. [Can’t Click]
//                // Deleted                      >  Not Show eRFX no.
//                // Rejected (SR Status)         >  Not Show eRFX no.
//                // Qualified Supplier           >  Disable eRFX No. [Can’t Click]
//                // No Qualified Supplier        >  Disable eRFX No. [Can’t Click]
//                // No Supplier Response         >  Disable eRFX No. [Can’t Click]
//                // No Supplier Selected         >  Disable eRFX No. [Can’t Click]
//
//                else if (pathUrl != null && pathUrl.equalsIgnoreCase(PathUrl.REVIEW_REQUEST.path()) && requestReviewerDto.isPresent()) {
//                    if (requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_RESPONSE.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_PENDING.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_ACTIVE.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_SHORTLIST.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_APPROVE_SHORTLIST.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_QUALIFIED_SUPPLIER.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_NO_QUALIFIED_SUPPLIER.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_NO_SUPPLIER_RESPONSE.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_NO_SUPPLIER_SELECTED.id()
//                    ) {
//                        sourcingStatusDto.setRedirectURL(null);
//                    }
//                }
//
//                // --------------------------------------------------------------------------------------
//                // Dept-approver
//                // Test Data > User : approver01 / Pass : 12345
//                // --------------------------------------------------------------------------------------
//                // None                         >  Not show eRFX no.
//                // Draft                        >  Show user don't have access to this item
//                // Awaiting Response            >  Disable eRFX No. [Can’t Click]
//                // Pending                      >  Disable eRFX No. [Can’t Click]
//                // Cancelled                    >  Disable eRFX No. [Can’t Click]
//                // Awaiting Active              >  Disable eRFX No. [Can’t Click]
//                // Awaiting Shortlist           >  Disable eRFX No. [Can’t Click]
//                // Awaiting Approve Shortlist   >  Disable eRFX No. [Can’t Click]
//                // Deleted                      >  Not Show eRFX no.
//                // Rejected (SR Status)         >  Not Show eRFX no.
//                // Qualified Supplier           >  Shortlist
//                // No Qualified Supplier        >  Shortlist
//                // No Supplier Response         >  Shortlist
//                // No Supplier Selected         >  Shortlist
//
//                else if (requestDeptApproverDto.isPresent()) {
//                    if (requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_RESPONSE.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_PENDING.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_ACTIVE.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_SHORTLIST.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_APPROVE_SHORTLIST.id()
//                    ) {
//                        sourcingStatusDto.setRedirectURL(null);
//                    }
//                }
//
//                // --------------------------------------------------------------------------------------
//                // Purchaser [Owner of request]
//                // Test Data > User : Supansa / Pass : 12345
//                // --------------------------------------------------------------------------------------
//                // None                         >  Not show eRFX no.
//                // Draft                        >  Edit eRFX detail
//                // Awaiting Response            >  View detail
//                // Pending                      >  View detail
//                // Cancelled                    >  Disable eRFX No. [Can’t Click]
//                // Awaiting Active              >  View detail
//                // Awaiting Shortlist           >  Shortlist
//                // Awaiting Approve Shortlist   >  Shortlist
//                // Deleted                      >  Not Show eRFX no.
//                // Rejected (SR Status)         >  Not Show eRFX no.
//                // Qualified Supplier           >  Shortlist
//                // No Qualified Supplier        >  Shortlist
//                // No Supplier Response         >  Shortlist
//                // No Supplier Selected         >  Shortlist
//
//                else if (request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName())) {
//
//                    // Special Case
//                    if (request.getDelegateActionBy() != null && !request.getDelegateActionBy().equalsIgnoreCase(AppUtil.getUserName())) {
//                        if (requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_RESPONSE.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_PENDING.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_ACTIVE.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_SHORTLIST.id() ||
//                            requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_APPROVE_SHORTLIST.id()
//                        ) {
//                            sourcingStatusDto.setRedirectURL(null);
//                        }
//
//                    } else if(requestItem.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id()) {
//                        sourcingStatusDto.setRedirectURL(null);
//                    }
//                }
//
////                else if (!request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) &&
////                        (request.getDelegateActionBy() != null && !request.getDelegateActionBy().equalsIgnoreCase(AppUtil.getUserName()))) {
////                    if (requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_RESPONSE.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_PENDING.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_ACTIVE.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_SHORTLIST.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_APPROVE_SHORTLIST.id()
////                    ) {
////                        sourcingStatusDto.setRedirectURL(null);
////                    }
////                }
//
//                // --------------------------------------------------------------------------------------
//                // Purchaser [Not Owner of request]
//                // Test Data > User : Duangkhae / Pass : 12345
//                // --------------------------------------------------------------------------------------
//                // None                         >  Not show eRFX no.
//                // Draft                        >  Show user don't have access to this item
//                // Awaiting Response            >  Disable eRFX No. [Can’t Click]
//                // Pending                      >  Disable eRFX No. [Can’t Click]
//                // Cancelled                    >  Disable eRFX No. [Can’t Click]
//                // Awaiting Active              >  Disable eRFX No. [Can’t Click]
//                // Awaiting Shortlist           >  Disable eRFX No. [Can’t Click]
//                // Awaiting Approve Shortlist   >  Disable eRFX No. [Can’t Click]
//                // Deleted                      >  Not Show eRFX no.
//                // Rejected (SR Status)         >  Not Show eRFX no.
//                // Qualified Supplier           >  Shortlist
//                // No Qualified Supplier        >  Shortlist
//                // No Supplier Response         >  Shortlist
//                // No Supplier Selected         >  Shortlist
//
//                else if (!request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) &&
//                        request.getDelegateActionBy() == null) {
//
//                    DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
//                    delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
//                    delegationActiveRequest.setDelegatorBy(request.getAssignedBy());
//
//                    DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
//                    if (delegationActiveResponse.getData() != null) {
//                        if (requestItem.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id()) {
//                            sourcingStatusDto.setRedirectURL(null);
//                        }
//                    } else {
//                        if (requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_RESPONSE.id() ||
//                                requestItem.getSourcingStatus().getRecId() == SOURCING_PENDING.id() ||
//                                requestItem.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id() ||
//                                requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_ACTIVE.id() ||
//                                requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_SHORTLIST.id() ||
//                                requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_APPROVE_SHORTLIST.id()
//                        ) {
//                            sourcingStatusDto.setRedirectURL(null);
//                        }
//                    }
//                }
//
//                // --------------------------------------------------------------------------------------
//                // Requester that create request
//                // Test Data > User : Pakorn / Pass : 12345
//                // --------------------------------------------------------------------------------------
//                // None                         >  Not show eRFX no.
//                // Draft                        >  Show user don't have access to this item
//                // Awaiting Response            >  Disable eRFX No. [Can’t Click]
//                // Pending                      >  Disable eRFX No. [Can’t Click]
//                // Cancelled                    >  Disable eRFX No. [Can’t Click]
//                // Awaiting Active              >  Disable eRFX No. [Can’t Click]
//                // Awaiting Shortlist           >  Disable eRFX No. [Can’t Click]
//                // Awaiting Approve Shortlist   >  Disable eRFX No. [Can’t Click]
//                // Deleted                      >  Not Show eRFX no.
//                // Rejected (SR Status)         >  Not Show eRFX no.
//                // Qualified Supplier           >  Shortlist
//                // No Qualified Supplier        >  Shortlist
//                // No Supplier Response         >  Shortlist
//                // No Supplier Selected         >  Shortlist
//
////                else if (request.getCreatedBy().equalsIgnoreCase(AppUtil.getUserName())) {
////                    if (requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_RESPONSE.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_PENDING.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_ACTIVE.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_SHORTLIST.id() ||
////                        requestItem.getSourcingStatus().getRecId() == SOURCING_AWAITING_APPROVE_SHORTLIST.id()
////                    ) {
////                        sourcingStatusDto.setRedirectURL(null);
////                    }
////                }
//
//            }
        } else if (requestItem.getSourcingType().getRecId() == SOURCING_TYPE_EXCEPTIONAL_SOURCING.id() && sourcingStatusDto.getRecId() == SOURCING_QUALIFIED_SUPPLIER.id()) {
            sourcingStatusDto.setCanReject(false);
            sourcingStatusDto.setCanEdit(false);
        }

        // Set isLastItemForBothDeletionAndReject property
        List<RequestItem> requestItemList = request.getRequestItemList();//requestItemRepository.getRequestItemByRequestId(request.getRecId());
        Long totalRemovedItem = requestItemList.stream().filter(ri -> ri.getSourcingStatus().getRecId() == SOURCING_DELETED.id() ||
                ri.getSourcingStatus().getRecId() == SOURCING_REJECTED.id()).count();
        boolean isRemovedLastItem = requestItemList.size() == (totalRemovedItem + 1) &&
                requestItem.getSourcingStatus().getRecId() != SOURCING_DELETED.id() &&
                requestItem.getSourcingStatus().getRecId() != SOURCING_REJECTED.id();
        sourcingStatusDto.setRemovedLastItem(isRemovedLastItem);

        return sourcingStatusDto;
    }

    private String generateERFXUrl(String serviceName, String authCode, String sourcingDocNo) {
        String eRFXUrl = CommonUtils.encodeValue(
                String.format("%s/ep/AuthLogin.action?SelectService=%s&erfx_id=%s",
                erfxConfig.getHostName(),
                serviceName,
                sourcingDocNo));

        return String.format("%s/?cookie=%s&path=%s", BASEURL_PTVN_COOKIE, authCode, eRFXUrl);
    }

//    private ERFXStatusResponse getERFXStatusList(ERFXStatusRequest eRFXStatusRequest, String refreshToken) {
//        String authHeader = "Bearer " + (refreshToken == null ? AppUtil.getJwtToken() : refreshToken);
//        return erfxClient.getErfxStatus(authHeader, eRFXStatusRequest);
//    }
}
