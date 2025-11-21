package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pantavanij.sourcingreq.services.client.DelegateClient;
import com.pantavanij.sourcingreq.services.client.ErfxClient;
import com.pantavanij.sourcingreq.services.config.ERFXConfig;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormFieldDTO;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormOptionChoiceDTO;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormSectionDTO;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormTabDTO;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingType;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unbescape.html.HtmlEscape;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.*;
import static com.pantavanij.sourcingreq.services.enums.AttachmentFlag.DDNO;
import static com.pantavanij.sourcingreq.services.enums.ERFXRedirectPath.*;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.*;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_DRAFT;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_ERFX;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.*;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.TENANT_REQUEST_PARTIAL_COMPLETED;
import static com.pantavanij.sourcingreq.services.enums.ThirdPartyRole.THIRD_PARTY_ROLE_DATA_CONTROLLER;
import static com.pantavanij.sourcingreq.services.enums.ThirdPartyRole.THIRD_PARTY_ROLE_DATA_PROCESSOR;
import static com.pantavanij.sourcingreq.services.util.CommonUtils.distinctByKey;
import static com.pantavanij.sourcingreq.services.util.Constant.DELETION_REASON;
import static com.pantavanij.sourcingreq.services.util.CommonUtils.mapOptionalToOptionDto;

@RequiredArgsConstructor
@Service
public class ERFXServiceImpl implements ERFXService {
    private final ERFXConfig erfxConfig;
    private final ErfxClient erfxClient;
    private final DelegateClient delegateClient;
    private final RequestRepository requestRepository;
    private final TenantApprovalStatusRepository tenantApprovalStatusRepository;
    private final RequestItemRepository requestItemRepository;
    private final SourcingStatusRepository sourcingStatusRepository;
    private final SourcingTypeRepository sourcingTypeRepository;
    private final ExistingPriceItemRepository existingPriceItemRepository;
    private final RequestItemService requestItemService;
    private final SourcingStatusService sourcingStatusService;
    private final ExistingPriceItemService existingPriceItemService;
    private final AttachmentService attachmentService;
    private final RequestItemAttachmentService requestItemAttachmentService;
    private final RequestService requestService;
    private final FileUtil fileUtil;
    private final RequestItemAdditionalService requestItemAdditionalService;
    private final UaaService uaaService;
    private final TenantService tenantService;
    private final RequestItemSubCategoryRepository requestItemSubCategoryRepository;
    private final TenantSubCategoryService tenantSubCategoryService;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final RequestHistoryService requestHistoryService;
    private final RequestAdditionalRepository requestAdditionalRepository;
    private final DelegationService delegationService;
    private final TenantConfigService tenantConfigService;
    private final RequestSourcingRepository requestSourcingRepository;
    private final RequestStatusService requestStatusService;

    private final String FOLDER_NAME = "attachments";

    private static final Logger logger = LoggerFactory.getLogger(ERFXServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createERFX(ERFXCreateRequest request, String authCode) {
        logger.info("CreateERFX Step1 : {} ", request);
        UserDto user = AppUtil.getUser();
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return null;
        }
        RequestItem requestItem = requestItemRepository.findRequestItemByRecId(request.getItems().get(0).getItemId().longValue());
        Request request_ = requestItem.getRequest();
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request_.getRecId());
        DelegationDto delegationDto = null;
        logger.info("CreateERFX Step2 requestPurchasers : {} ", requestPurchasers);

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request_.getAssignedBy());

        boolean isDelegationActive = false;
        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
            isDelegationActive = true;
        }
        logger.info("CreateERFX Step3 isDelegationActive : {} ", isDelegationActive);

        boolean isOwnerPurchaser = false;
        if (request_.getDelegateActionBy() != null) {
            isOwnerPurchaser = AppUtil.isPurchaser() && request_.getDelegateActionBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName());
        } else {
            if(isDelegationActive) {
                isOwnerPurchaser = AppUtil.isPurchaser() && request_.getApprovalStatus().getName().equals(TENANT_APPROVAL_AWAITING.code());
            } else {
                isOwnerPurchaser = AppUtil.isPurchaser() &&
                        ((request_.getAssignedBy() != null && request_.getAssignedBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName())));
            }
        }
        logger.info("CreateERFX Step4 isOwnerPurchaser : {} ", isOwnerPurchaser);

        if (AppUtil.isAllowedAction(user, request_, requestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_CONVERT_ERFX)) {
            if (isOwnerPurchaser) {
                logger.info("CreateERFX Step5 isAllowAction => true & isOwnerPurchaser => true");
                RequesterRequestDto requestDto = requestService.findRequestSourcingByRecId(requestItem.getRequest().getRecId());
                String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
                request.setRequesterName(requestDto.getRequester());
                request.setDepartment(requestDto.getDepartment());
                request.setPhone(requestDto.getPhone());
                request.setMobile(requestDto.getMobile());
                request.setEmail(requestDto.getEmail());
                request.setTargetDate(requestDto.getExpectedDate() != null ? DateTimeUtil.convertTimestampByUserTimeZoneStr(requestDto.getExpectedDate(), timeZone) : null);
                request.setReminder(requestDto.getRequestNo());

                String tenantCode = AppUtil.getTenantId();
                Tenant tenant = tenantService.findByCode(tenantCode);
                boolean isSend = tenantConfigService.isSendOrganizationToERFX(tenant.getRecId());
                if (isSend) {
                    request.setOrganization(requestDto.getOrganizationObj().getLabel());
                }

                List<ERFXAttachmentDto> attachments = request.getAttachments();
                setUrlAttachmentCreateERFX(attachments);
                String authHeader = "Bearer " + AppUtil.getJwtToken();
                ERFXCreateResponse response = new ERFXCreateResponse();
                try {
                    response = erfxClient.createErfx(authHeader, request);
                    logger.info("CreateERFX Step7 erfxClient.createErfx => Success : {}", response);
                }
                catch(Exception ex) {
                    logger.info("CreateERFX Step7 erfxClient.createErfx => Failed : {}", ex.getMessage());
                }

                boolean isShowStep3 = tenantConfigService.getStep3(tenant.getRecId());
                logger.info("CreateERFX Step8 isShowStep3 : {} ", isShowStep3);
                if (isShowStep3) {
                    if (response.getItems().size() > 0) {
                        ERFXAdditionalDataRequest additionalDataRequest = new ERFXAdditionalDataRequest();
                        additionalDataRequest.setErfxId(response.getErfxNum());
                        additionalDataRequest.setProjectCode(requestDto.getProjectCode());
                        additionalDataRequest.setProjectName(requestDto.getProjectName());
                        additionalDataRequest.setProjectLabel(requestDto.getProjectCode() != null ? requestDto.getProjectCode() + "-" + requestDto.getProjectName() : requestDto.getProjectName());
                        //additionalDataRequest.setProjectCode(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() : null);
                        additionalDataRequest.setObjective(requestDto.getObjective());
                        additionalDataRequest.setOutsourceService(requestDto.getOutsourceService() != null ? ("true".equals(requestDto.getOutsourceService().getValue()) ? 1 : 0) : 0);
                        additionalDataRequest.setOutsourceCode(requestDto.getOutsourceService() != null ? requestDto.getOutsourceService().getName() : null);
                        additionalDataRequest.setRequireSuppPerfEva(requestDto.getPerformanceEvaluation() != null ? ("true".equals(requestDto.getPerformanceEvaluation().getValue()) ? 1 : 0) : 0);
                        additionalDataRequest.setEvaluationDate(requestDto.getEvaluationDate() != null ? DateTimeUtil.convertTimestampByUserTimeZoneStr(requestDto.getEvaluationDate(), timeZone) : null);
                        additionalDataRequest.setMakingContract(requestDto.getMakingContract() != null ? ("true".equals(requestDto.getMakingContract().getValue()) ? 1 : 0) : 0);
                        additionalDataRequest.setMakingContractReason(requestDto.getMakingContractReason());
                        additionalDataRequest.setMakingRptContract(requestDto.getMakingRptContract() != null ? ("true".equals(requestDto.getMakingRptContract().getValue()) ? 1 : 0) : 0);
                        additionalDataRequest.setMakingRptContractReason(requestDto.getMakingRptContractReason());

                        logger.info("CreateERFX Step8.1 additionalDataRequest : {} ", additionalDataRequest);

                        // PDPA Fields
                        boolean isAttachPDPA = request.getAttachments().stream()
                                .anyMatch(attachment -> (attachment != null && attachment.getFlag() != null) && attachment.getFlag().equalsIgnoreCase(DDNO.code()));

                        boolean isSwapThirdPartyRole = false;
                        switch (requestDto.getRelatePdpa() != null ? requestDto.getRelatePdpa() : "") {
                            case "Yes":
                                additionalDataRequest.setRelatePdpa(0);
                                break;
                            case "No":
                                additionalDataRequest.setRelatePdpa(1);
                                break;
                            default:
                                additionalDataRequest.setRelatePdpa(2);
                                break;
                        }
                        //additionalDataRequest.setRelatePdpa(requestDto.getRelatePdpa());

                        logger.info("CreateERFX Step8.2 additionalDataRequest : {} ", additionalDataRequest);

                        switch (requestDto.getThirdPartyRole() != null ? requestDto.getThirdPartyRole() : "") {
                            case "Data Processor":
                                additionalDataRequest.setThirdPartyRole("DP");
                                if (!isAttachPDPA) {
                                    isSwapThirdPartyRole = true;
                                    additionalDataRequest.setThirdPartyRole("DC");
                                }
                                break;
                            case "Data Controller":
                                additionalDataRequest.setThirdPartyRole("DC");
                                if (isAttachPDPA) {
                                    isSwapThirdPartyRole = true;
                                    additionalDataRequest.setThirdPartyRole("DP");
                                }
                                break;
                            case "Not Applicable":
                                additionalDataRequest.setThirdPartyRole("NA");
                                break;
                        }
                        //additionalDataRequest.setThirdPartyRole(requestDto.getThirdPartyRole());

                        logger.info("CreateERFX Step8.3 additionalDataRequest : {} ", additionalDataRequest);

                        switch (requestDto.getDpaType() != null ? requestDto.getDpaType() : "") {
                            case "DPA with SCC":
                                if(!isSwapThirdPartyRole) {
                                    additionalDataRequest.setDpaType("DPASCC");
                                } else {
                                    additionalDataRequest.setDpaType("NA");
                                }
                                break;
                            case "DPA":
                                if(!isSwapThirdPartyRole) {
                                    additionalDataRequest.setDpaType("DPA");
                                } else {
                                    additionalDataRequest.setDpaType("NA");
                                }
                                break;
                            case "Not Applicable":
                                additionalDataRequest.setDpaType("NA");
                                break;
                        }
                        //additionalDataRequest.setDpaType(requestDto.getDpaType());

                        logger.info("CreateERFX Step8.4 additionalDataRequest : {} ", additionalDataRequest);

                        int vatAbsorbedBy = 0;
                        switch (requestDto.getVatAbsorbedBy() != null ? requestDto.getVatAbsorbedBy().getValue() : "") {
                            case "BAY & Subs.":
                                vatAbsorbedBy = 1;
                                break;
                            case "Vendors":
                                vatAbsorbedBy = 2;
                                break;
                            default:
                                break;
                        }
                        additionalDataRequest.setVatAbsorbedBy(vatAbsorbedBy);

                        logger.info("CreateERFX Step8.5 additionalDataRequest : {} ", additionalDataRequest);

                        int stampDutyAbsorbedBy = 0;
                        switch (requestDto.getStampDuty() != null ? requestDto.getStampDuty().getValue() : "") {
                            case "BAY & Subs.":
                                stampDutyAbsorbedBy = 1;
                                break;
                            case "Vendors":
                                stampDutyAbsorbedBy = 2;
                                break;
                            default:
                                break;
                        }
                        additionalDataRequest.setStampDutyAbsorbedBy(stampDutyAbsorbedBy);

                        logger.info("CreateERFX Step8.6 additionalDataRequest : {} ", additionalDataRequest);

                        int whtAbsorbedBy = 0;
                        switch (requestDto.getWhtAbsorbedBy() != null ? requestDto.getWhtAbsorbedBy().getValue() : "") {
                            case "BAY & Subs.":
                                whtAbsorbedBy = 1;
                                break;
                            case "Vendors":
                                whtAbsorbedBy = 2;
                                break;
                            case "No":
                                whtAbsorbedBy = 3;
                                break;
                            case "Not Specific":
                                whtAbsorbedBy = 4;
                                break;
                            default:
                                break;
                        }
                        additionalDataRequest.setWhtAbsorbedBy(whtAbsorbedBy);

                        logger.info("CreateERFX Step8.7 additionalDataRequest : {} ", additionalDataRequest);

                        additionalDataRequest.setNeedWhtCert(requestDto.getNeedWhtCert() != null ? ("true".equals(requestDto.getNeedWhtCert().getValue()) ? 1 : 0) : 0);
                        List<ERFXAdditionalDataItemDto> items = new ArrayList<>();
                        List<RequestItemV2Dto> requestItemDtoList = requestItemService.getRequestItemByRequestId(requestDto.getRecId());
                        this.setOptionalObjs(requestItemDtoList);

                        for (ERFXItemDto requestItemBody : request.getItems()) {
                            RequestItemV2Dto requestItemDto = requestItemDtoList.stream().filter(requestItemList -> requestItemList.getRecId() == requestItemBody.getItemId().intValue()).findFirst().get();
                            ERFXAdditionalDataItemDto item = new ERFXAdditionalDataItemDto();
                            ERFXItemResponse erfxItem = response.getItems().stream().filter(erfxItemResponse -> erfxItemResponse.getItemId() == requestItemDto.getRecId().intValue()).findFirst().get();
                            item.setItemId(erfxItem.getErfxItemId());
                            item.setBidValidityStartDate(requestItemDto.getBidValidityStartDate() != null ? requestItemDto.getBidValidityStartDate() : null);
                            item.setBidValidityEndDate(requestItemDto.getBidValidityEndDate() != null ? requestItemDto.getBidValidityEndDate() : null);
                            item.setCategory(requestItemDto.getCategoryObj() != null ? requestItemDto.getCategoryObj().getName() : null);
                            item.setSubCategory(requestItemDto.getSubCategoryObj() != null ? requestItemDto.getSubCategoryObj().getName() : null);
                            items.add(item);
                        }
                        additionalDataRequest.setItems(items);

                        logger.info("CreateERFX Step8.8 additionalDataRequest : {} ", additionalDataRequest);

                        List<ERFXQuestionnaireQuestionRequest> questions = new ArrayList<>();
                        if (requestDto.getQuestionnaire() != null) {
                            if (requestDto.getQuestionnaire().getFormTabs() != null && requestDto.getQuestionnaire().getFormTabs().size() > 0) {
                                for (FormTabDTO formTabDTO : requestDto.getQuestionnaire().getFormTabs()) {
                                    if (formTabDTO.getFormSections() != null && formTabDTO.getFormSections().size() > 0) {
                                        for (FormSectionDTO formSectionDTO : formTabDTO.getFormSections()) {
                                            if (formSectionDTO.getFormFields() != null && formSectionDTO.getFormFields().size() > 0) {
                                                for (FormFieldDTO formFieldDTO : formSectionDTO.getFormFields()) {
                                                    ERFXQuestionnaireQuestionRequest question = new ERFXQuestionnaireQuestionRequest();
                                                    question.setQuestion(formFieldDTO.getFieldName());
                                                    String questionType = "";
                                                    if (StringUtils.isEmpty(formFieldDTO.getFieldTypeName())) {
                                                        formFieldDTO.setFieldTypeName("Short Answer");
                                                    }
                                                    switch (formFieldDTO.getFieldTypeName()) {
                                                        case "Short Answer":
                                                            questionType = "text_box";
                                                            break;
                                                        case "Numeric":
                                                            questionType = "num_text_box";
                                                            break;
                                                        case "Checkboxes":
                                                            questionType = "check_box";
                                                            break;
                                                        case "Multiple Choice":
                                                            questionType = "multiple_choice";
                                                            break;
                                                        case "File Upload":
                                                            questionType = "doc_required";
                                                            break;
                                                        default:
                                                            questionType = "text_box";
                                                            break;
                                                    }
                                                    question.setQuestionType(questionType);
                                                    question.setQuestionHelp(formFieldDTO.getFieldDesc());
                                                    question.setQuestionWeight(formFieldDTO.getWeight().intValue() != 0 ? formFieldDTO.getWeight().intValue() : null);
                                                    question.setQuestionOrder(formFieldDTO.getSequence());
                                                    question.setRequireField(formFieldDTO.getIsRequire() != null && formFieldDTO.getIsRequire() ? "Y" : "N");
                                                    question.setOther("N");//TODO
                                                    List<ERFXQuestionnaireAnswerRequest> answers = new ArrayList<>();
                                                    if (formFieldDTO.getOptionChoices() != null && !formFieldDTO.getOptionChoices().isEmpty()) {
                                                        for (FormOptionChoiceDTO formOptionChoiceDTO : formFieldDTO.getOptionChoices()) {
                                                            ERFXQuestionnaireAnswerRequest answer = new ERFXQuestionnaireAnswerRequest();
                                                            answer.setAnswer(formOptionChoiceDTO.getChoiceName());
                                                            answer.setAnswerHelp(null);//TODO
                                                            answer.setAnswerPoint(formOptionChoiceDTO.getPoint() != null &&
                                                                    formOptionChoiceDTO.getPoint().intValue() != 0 ? formOptionChoiceDTO.getPoint().intValue() : null);
                                                            answer.setAnswerOrder(formOptionChoiceDTO.getSequence());
                                                            answers.add(answer);
                                                        }
                                                    }
                                                    question.setAnswers(answers);
                                                    questions.add(question);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!questions.isEmpty()) {
                            additionalDataRequest.setQuestionnaire(questions);
                        }

                        additionalDataRequest.setBackground(requestDto.getBackground());

                        logger.info("CreateERFX Step8.9 additionalDataRequest : {} ", additionalDataRequest);

                        try {
                            ERFXAdditionalDataResponse additionalDataResponse = erfxClient.additionalDataErfx(authHeader, additionalDataRequest);
                            logger.info("CreateERFX Step9 erfxClient.createErfx => Success : {} ", additionalDataResponse);
                        }
                        catch(Exception ex) {
                            logger.info("CreateERFX Step9 erfxClient.createErfx => Failed : {} ", ex.getMessage());
                        }

                        Request _request = requestRepository.findRequestsByRecId(requestDto.getRecId());
                        Optional<RequestAdditional> requestAdditional = requestAdditionalRepository.findTop1ByRequestId(requestDto.getRecId());
                        if (requestAdditional.isPresent()) {
                            if (requestAdditional.get().getThirdPartyRole() != null) {
                                boolean isSent = request.getAttachments().stream()
                                        .anyMatch(attachment -> (attachment != null && attachment.getFlag() != null) && attachment.getFlag().equalsIgnoreCase(DDNO.code()));
                                if (requestAdditional.get().getThirdPartyRole().trim().equalsIgnoreCase(THIRD_PARTY_ROLE_DATA_PROCESSOR.code().trim())) {
                                    if (!isSent) {
                                        requestHistoryService.saveRequestHistoryByAction(_request, PDPA_NO_ATTACH.id(), response.getErfxNum().toString());
                                    }
                                } else if (requestAdditional.get().getThirdPartyRole().trim().equalsIgnoreCase(THIRD_PARTY_ROLE_DATA_CONTROLLER.code().trim())) {
                                    if (isSent) {
                                        requestHistoryService.saveRequestHistoryByAction(_request, PDPA_IS_ATTACH.id(), response.getErfxNum().toString());
                                    }
                                }
                            }
                        }

                        logger.info("CreateERFX End.");
                    }
                }
                return saveERFXResponse(authCode, response);
            } else {
                logger.info("CreateERFX Step5 isAllowAction => true & isOwnerPurchaser => false");
                // Cannot convert to eRFX/Existing Price due to some of the items has been done by another users.
                throw new BusinessException(ApiMessage.E7088, ApiMessage.E7088.description());
            }
        }
        logger.info("CreateERFX Step5 isAllowAction => false");

        return null;
    }

    private String saveERFXResponse(String authCode, ERFXCreateResponse eRFXResponse) {
        String redirectURL = "";
        if (eRFXResponse != null) {

            logger.info("CreateERFX saveERFXResponse start : {}", eRFXResponse);
            for (ERFXItemResponse eRFXItem : eRFXResponse.getItems()) {
                RequestItem requestItem = requestItemRepository.findRequestItemByRecId(eRFXItem.getItemId().longValue());
                if (requestItem != null) {
                    // Initialize new ExistingPriceItem object
                    ExistingPriceItemRequest existingPriceItemRequest = new ExistingPriceItemRequest();
                    existingPriceItemRequest.setRecId(0L);
                    existingPriceItemRequest.setRequestId(requestItem.getRequest().getRecId());
                    existingPriceItemRequest.setRequestItemId(requestItem.getRecId());
                    existingPriceItemRequest.setTenantId(requestItem.getTenant().getRecId());
                    existingPriceItemRequest.setItemName(requestItem.getItemName());
                    existingPriceItemRequest.setItemDescription(requestItem.getItemDescription());
                    existingPriceItemRequest.setBrand(requestItem.getBrand());
                    existingPriceItemRequest.setPartNo(requestItem.getPartNo());
                    existingPriceItemRequest.setQuantity(requestItem.getQuantity());

//                    Supplier supplier = supplierRepository.getFirstSupplierByTenantId(requestItem.getTenant().getRecId());
//                    existingPriceItemRequest.setTpShortName(supplier.getShortName());
                    existingPriceItemRequest.setUnitObj(OptionDtoMapper.INSTANCE.toUnitOptionDto(requestItem.getUnit()));

//                    ERFXItemDto eRFXItemDto = getERFXItemDtoByRequestItemId(eRFXRequest, requestItem);
                    existingPriceItemRequest.setUnitPrice(new BigDecimal(0));
                    existingPriceItemRequest.setCurrencyObj(OptionDtoMapper.INSTANCE.toCurrencyOptionDto(requestItem.getRequest().getCurrency()));
                    existingPriceItemRequest.setSourcingDocNo(eRFXResponse.getErfxNum().toString());
                    existingPriceItemRequest.setSourcingTypeId(SOURCING_TYPE_ERFX.id());
                    existingPriceItemRequest.setOrganizationId(requestItem.getRequest().getOrganizationId());
                    existingPriceItemService.saveExistingPriceItem(existingPriceItemRequest, false);

                    // Update eRFX data in existing RequestItem object
                    requestItem.setSourcingDocNo(eRFXResponse.getErfxNum().toString());
                    requestItem.setSourcingDocId(eRFXItem.getErfxItemId().toString());
                    SourcingStatus sourcingStatus = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_DRAFT.id());
                    requestItem.setSourcingStatus(sourcingStatus);
                    SourcingType sourcingType = sourcingTypeRepository.findSourcingTypeByRecId(SOURCING_TYPE_ERFX.id());
                    requestItem.setSourcingType(sourcingType);
                    requestItem.setUpdatedBy(AppUtil.getUserName());
                    requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    requestItemRepository.save(requestItem);

                    // Update Approval Status to Partial Completed
                    Request request = requestRepository.findRequestsByRecId(requestItem.getRequest().getRecId());
                    TenantApprovalStatus tenantApprovalStatus =
                            tenantApprovalStatusRepository.findByNameAndTenant(TENANT_APPROVAL_PARTIAL_COMPLETED.code(), request.getTenant());
                    request.setApprovalStatusId(tenantApprovalStatus.getApprovalStatus().getRecId());
                    request.setApprovalStatus(tenantApprovalStatus);
                    request.setUpdatedBy(AppUtil.getUserName());
                    request.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    if (request.getAssignedBy() != null && !request.getAssignedBy().equalsIgnoreCase(AppUtil.getUserName()) && request.getDelegateActionBy() == null) {
                        request.setDelegateActionBy(AppUtil.getUserName());
                        String authHeader = "Bearer " + AppUtil.getJwtToken();
                        DelegationActiveCreateDateRequest delegationActiveCreateDateRequest = new DelegationActiveCreateDateRequest();
                        delegationActiveCreateDateRequest.setDelegateeBy(AppUtil.getUserName());
                        delegationActiveCreateDateRequest.setDelegatorBy(request.getAssignedBy());
                        DelegationActiveCreateDateResponse delegationActiveCreateDateResponse = delegateClient.getDelegationActiveCreateDate(authHeader, delegationActiveCreateDateRequest);
                        if (delegationActiveCreateDateResponse.getCreatedDate() != null) {
//                            request.setDelegateActionDate(Timestamp.valueOf(delegationActiveCreateDateResponse.getCreatedDate().toLocalDateTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
                            request.setDelegateActionDate(delegationActiveCreateDateResponse.getCreatedDate());
                        } else {
                            request.setDelegateActionDate(DateTimeUtil.getTimestampUTC());
                        }
                    }
                    requestRepository.save(request);
                }
            }

            logger.info("CreateERFX saveERFXResponse end.");

            // Generate eRFX URL for DRAFT mode
            redirectURL = getDraftURL(authCode, eRFXResponse.getErfxNum().toString());

            logger.info("CreateERFX redirectURL : {}", redirectURL);
        }
        return redirectURL;
    }

    private void setUrlAttachmentCreateERFX(List<ERFXAttachmentDto> attachments) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        for (ERFXAttachmentDto attachment : attachments) {
            try {
                if (!attachment.getUrl().isEmpty()) {
                    String customFolderName = String.format("%s/%s", FOLDER_NAME, tenant.getCode());
                    String attachmentURL = fileUtil.getObjectURL(attachment.getUrl(), customFolderName);
                    attachment.setUrl(attachmentURL);
                }
            } catch (Exception ex) {
                ex.getMessage();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateERFXInfo(ERFXUpdateInfoRequest erfxUpdateInfoRequest) throws Exception {

        if(erfxUpdateInfoRequest.getErfxNum() != null) {
//            Tenant tenant = tenantService.findByCode(tenantCode);
            RequestSourcing requestSourcing = null;

            List<RequestItem> requestItemList = requestItemRepository.getRequestItemBySourcingDocNoAndSourcingTypeId(erfxUpdateInfoRequest.getErfxNum(), SOURCING_TYPE_ERFX.id());
            if (requestItemList.size() > 0) {

                // Update Sourcing Status
                if(erfxUpdateInfoRequest.getStatus() != null) {
                    Request request = requestRepository.findRequestsByRecId(requestItemList.get(0).getRequest().getRecId());
                    SourcingStatus supplierStatus = sourcingStatusRepository.findSourcingStatusByCode(erfxUpdateInfoRequest.getStatus());
                    requestItemList.forEach(requestItem -> requestItem.setSourcingStatus(supplierStatus));
                    requestItemRepository.saveAll(requestItemList);
                    requestStatusService.updateRequestAndApprovalStatus(request.getRequestItemList(), request);
                }

                // Update Company Code & name
                Optional<RequestSourcing> optionalRequestSourcing = requestSourcingRepository.findBySourcingDocNo(erfxUpdateInfoRequest.getErfxNum().toString());
                OrganizationClientDto organizationClientDto =
                        uaaService.getOrganizationByTenantIdAndUserName(AppUtil.getTenantId(), AppUtil.getIdp(), AppUtil.getUserName());

                String companyName = HtmlEscape.unescapeHtml(erfxUpdateInfoRequest.getCompanyName().trim());
                String companyCode = organizationClientDto.getBorgUserList().stream()
                        .filter(borgUser -> String.valueOf(borgUser.getBorgName().trim()).equalsIgnoreCase(companyName))
                        .findFirst()
                        .map(BorgUserAdditionalDto::getBorgCode)
                        .orElse("N/A");

                if (optionalRequestSourcing.isPresent()) {
                    requestSourcing = optionalRequestSourcing.get();
                    requestSourcing.setUpdatedBy(AppUtil.getUserName());
                    requestSourcing.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    requestSourcing.setCompanyCode(companyCode);
                    requestSourcing.setCompanyName(companyName);
                } else {
                    requestSourcing = RequestSourcing.builder()
                            .tenant(requestItemList.get(0).getTenant())
                            .request(requestItemList.get(0).getRequest())
                            .sourcingDocNo(erfxUpdateInfoRequest.getErfxNum().toString())
                            .companyCode(companyCode)
                            .companyName(companyName)
                            .createdBy(AppUtil.getUserName())
                            .createdDate(DateTimeUtil.getTimestampUTC())
                            .build();
                }
                requestSourcingRepository.save(requestSourcing);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean updateERFXStatus( ERFXUpdateStatusRequest erfxUpdateStatusRequest) throws Exception {
        if(erfxUpdateStatusRequest.getData() != null && erfxUpdateStatusRequest.getData().size() > 0) {
            for (ERFXRequest erfxRequest : erfxUpdateStatusRequest.getData()) {
                if (erfxRequest.getErfxNum() != null) {
                    RequestSourcing requestSourcing = null;

                    List<RequestItem> requestItemList = requestItemRepository.getRequestItemBySourcingDocNoAndSourcingTypeId(erfxRequest.getErfxNum(), SOURCING_TYPE_ERFX.id());
                    if (requestItemList.size() > 0) {

                        // Update Sourcing Status
                        if (erfxRequest.getStatus() != null) {
                            Request request = requestRepository.findRequestsByRecId(requestItemList.get(0).getRequest().getRecId());
                            SourcingStatus supplierStatus = sourcingStatusRepository.findSourcingStatusByCode(erfxRequest.getStatus());
                            requestItemList.forEach(requestItem -> requestItem.setSourcingStatus(supplierStatus));
                            requestItemRepository.saveAll(requestItemList);
                            requestStatusService.updateRequestAndApprovalStatus(request.getRequestItemList(), request);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean receiveERFX(String sourcingDocNo, ERFXReceiveRequest request) throws Exception {
        logger.info("========== Receive ERFX ==========");
        logger.info("SourcingDocNo : " + sourcingDocNo);
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Request Body : " + (request != null ? objectMapper.writeValueAsString(request) : "{}"));
        List<ShortlistDto> shortlistDtoList = request.getErfxItems().stream().filter(distinctByKey(ShortlistDto::getItemId)).collect(Collectors.toList());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Integer tenantId = null;
        Long requestId = null;
        Long requestItemId = null;
        RequestItem requestItem = null;

        // -------------------- sourcingStatus ----------------------- //
        SourcingStatus sourcingStatusQualifiedSupplier = sourcingStatusService.getSourcingStatusById(SOURCING_QUALIFIED_SUPPLIER.id());
        SourcingStatus sourcingStatusNoQualifiedSupplier = sourcingStatusService.getSourcingStatusById(SOURCING_NO_QUALIFIED_SUPPLIER.id());
        SourcingStatus sourcingStatusNoSupplierResponse = sourcingStatusService.getSourcingStatusById(SOURCING_NO_SUPPLIER_RESPONSE.id());
        SourcingStatus sourcingStatusNoSupplierSelected = sourcingStatusService.getSourcingStatusById(SOURCING_NO_SUPPLIER_SELECTED.id());
        if (sourcingStatusQualifiedSupplier == null || sourcingStatusNoQualifiedSupplier == null || sourcingStatusNoSupplierResponse == null || sourcingStatusNoSupplierSelected == null) {
            throw new BusinessException(ApiMessage.E7042, ApiMessage.E7042.description());
        }

        List<Attachment> headerAttachmentList = null;
        for (ShortlistDto shortlist : shortlistDtoList) {
            // -------------------- requestItem ----------------------- //
            if (shortlist.getItemStatus() != null && shortlist.getItemStatus().equalsIgnoreCase("No Supplier Response")) {
                requestItemService.updateRequestItem(shortlist, sourcingStatusNoSupplierResponse, sourcingDocNo);
                continue;
            } else if (shortlist.getItemStatus() != null && shortlist.getItemStatus().equalsIgnoreCase("No Supplier Selected")) {
                requestItemService.updateRequestItem(shortlist, sourcingStatusNoSupplierSelected, sourcingDocNo);
                continue;
            } else if (shortlist.getItemStatus() != null && shortlist.getItemStatus().equalsIgnoreCase("Qualified Supplier")) {
                requestItem = requestItemService.updateRequestItem(shortlist, sourcingStatusQualifiedSupplier, sourcingDocNo);
            } else if (shortlist.getItemStatus() != null && shortlist.getItemStatus().equalsIgnoreCase("No Qualified Supplier")) {
                requestItem = requestItemService.updateRequestItem(shortlist, sourcingStatusNoQualifiedSupplier, sourcingDocNo);
            }

            tenantId = requestItem.getTenant().getRecId();
            requestItemId = requestItem.getRecId();

            // -------------------- requestItemAdditional ------------------//
            requestItemAdditionalService.saveOrUpdate(requestItem, shortlist, timeZone);

            // -------------------- existingPrice ----------------------- //
            String unitCode = shortlist.getUnit();
            requestId = requestItem.getRequest().getRecId();
            existingPriceItemService.updateExistingPriceItem(
                    shortlist,
                    unitCode,
                    requestId,
                    requestItemId,
                    tenantId,
                    request.getAwardedType(),
                    requestItem.getSourcingDocNo()
            );

            // -------------------------- Save All header attachment for each RequestItem ------------------------- //
            if(headerAttachmentList == null) {
                headerAttachmentList = attachmentService.saveAttachmentERFX(request.getErfxAttachments(), tenantId);
            }
            saveShortlistERFXHeaderAttachments(requestItemId, headerAttachmentList);

            // -------------------------- Save Item Attachment for each RequestItem------------------------- //
            saveShortlistERFXAttachments(requestItemId, tenantId, shortlist.getErfxItemAttachments());
        }

        // Insert new Request Sourcing (if it's available)
        if (request.getWithdraw() != null && requestItem != null) {
            RequestSourcing requestsourcing = null;
            Optional<RequestSourcing> optionalRequestSourcing = requestSourcingRepository.findBySourcingDocNo(sourcingDocNo);
            OrganizationClientDto organizationClientDto =
                    uaaService.getOrganizationByTenantIdAndUserName(AppUtil.getTenantId(), AppUtil.getIdp(), AppUtil.getUserName());

            String companyName = HtmlEscape.unescapeHtml(request.getCompanyName().trim());
            String companyCode = organizationClientDto.getBorgUserList().stream()
                    .filter(borgUser -> String.valueOf(borgUser.getBorgName().trim()).equalsIgnoreCase(companyName))
                    .findFirst()
                    .map(BorgUserAdditionalDto::getBorgCode)
                    .orElse("N/A");

            if(optionalRequestSourcing.isPresent()) {
                requestsourcing = optionalRequestSourcing.get();
                requestsourcing.setWithdraw(request.getWithdraw().equalsIgnoreCase("Yes"));
                requestsourcing.setWithdrawReason(HtmlEscape.unescapeHtml(request.getWithdrawReason()));
                requestsourcing.setCompanyCode(companyCode);
                requestsourcing.setCompanyName(companyName);
                requestsourcing.setUpdatedBy(AppUtil.getUserName());
                requestsourcing.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            } else {
                requestsourcing = RequestSourcing.builder()
                        .tenant(requestItem.getTenant())
                        .request(requestItem.getRequest())
                        .sourcingDocNo(sourcingDocNo)
                        .withdraw(request.getWithdraw().equalsIgnoreCase("Yes"))
                        .withdrawReason(HtmlEscape.unescapeHtml(request.getWithdrawReason()))
                        .companyCode(companyCode)
                        .companyName(companyName)
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .build();
            }
            requestSourcingRepository.save(requestsourcing);
        }

        // Update RequestStatus & SourcingStatus from eRFX
//        if(requestItem != null) {
//            requestService.updateRequestStatusByERFX(requestItem.getRequest());
//        }
        return true;
    }

    private void saveShortlistERFXAttachments(Long requestItemId, Integer tenantId, List<ERFXAttachmentDto> attachmentDtoList) {
        if (attachmentDtoList != null && !attachmentDtoList.isEmpty()) {
            List<Attachment> attachmentList = attachmentService.saveAttachmentERFX(attachmentDtoList, tenantId);
            requestItemAttachmentService.saveRequestItemAttachmentList(attachmentList, requestItemId);
        }
    }

    private void saveShortlistERFXHeaderAttachments(Long requestItemId, List<Attachment> attachmentList) {
        if (attachmentList != null && !attachmentList.isEmpty()) {
            requestItemAttachmentService.saveRequestItemAttachmentList(attachmentList, requestItemId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelERFX(Long erfxNo, ERFXCancelRequest eRFXCancelRequest) {
        List<RequestItem> requestItemList = requestItemRepository.getRequestItemBySourcingDocNoAndSourcingTypeId(erfxNo, SOURCING_TYPE_ERFX.id());
        if (requestItemList.size() > 0) {
            SourcingStatus sourcingStatusCancelled = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_CANCELLED.id());
            SourcingStatus sourcingStatusDeleted = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_DELETED.id());
            SourcingStatus sourcingStatusNone = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_NONE.id());

//            List<Long> eRFXDocNums = requestItemList
//                    .stream()
//                    .filter(r -> StringUtils.isNotEmpty(r.getSourcingDocNo()))
//                    .map(RequestItem::getSourcingDocNo)
//                    .map(Long::parseLong)
//                    .distinct()
//                    .collect(Collectors.toList());
//            ERFXStatusRequest eRFXStatusRequest = ERFXStatusRequest.builder().erfxNum(eRFXDocNums).build();
//            ERFXStatusResponse response = this.getERFXStatusList(eRFXStatusRequest, null);


            for (RequestItem requestItem : requestItemList) {

                // Update eRFX Status before perform eRFX Cancellation & Deletion
//                if (response != null &&
//                        requestItem.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) &&
//                        requestItem.getSourcingDocNo() != null) {
//                    existingPriceItemService.getERFXSourcingStatus(response.getData(), requestItem);
//                }

                if (requestItem.getSourcingStatus().getRecId() != SOURCING_DRAFT.id()) {
                    requestItem.setSourcingStatus(sourcingStatusCancelled);
                    String unescapedText = HtmlEscape.unescapeHtml(eRFXCancelRequest.getReason());
                    requestItem.setCancellationReason(unescapedText);

                } else {
                    if (TENANT_APPROVAL_COMPLETED.code().equals(requestItem.getRequest().getApprovalStatus().getName()) ||
                            TENANT_REQUEST_PARTIAL_COMPLETED.code().equals(requestItem.getRequest().getRequestStatus().getName())) {
                        requestItem.setSourcingStatus(sourcingStatusDeleted);
                        requestItem.setDeletionReason(String.format(DELETION_REASON, requestItem.getSourcingDocNo()));
                    } else {
                        requestItem.setSourcingStatus(sourcingStatusNone);
                        requestItem.setSourcingDocNo(null);
                        requestItem.setSourcingDocId(null);
                    }
                    SourcingType sourcingType = sourcingTypeRepository.getById(SOURCING_TYPE_DRAFT.id());
                    requestItem.setSourcingType(sourcingType);
                    Long requestId = requestItem.getRequest().getRecId();
                    Long requestItemId = requestItem.getRecId();
                    Integer tenantId = requestItem.getTenant().getRecId();
                    ExistingPriceItem existingPriceItem = existingPriceItemRepository.getExistingPriceItemByRequestIdAndRequestItemIdAndNullableSourcingDocNo(requestId, requestItemId, tenantId, requestItem.getSourcingDocNo());
                    if (existingPriceItem != null && existingPriceItem.getRecId() != null) {
                        existingPriceItemRepository.deleteExistingPriceItemByRecId(existingPriceItem.getRecId());
                    }
                }

                requestItem.setUpdatedBy(requestItem.getRequest().getAssignedBy());
                requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                requestItemRepository.save(requestItem);
            }

            // Update RequestStatus & SourcingStatus from eRFX
//            Request request = requestItemList.stream().findFirst().get().getRequest();
//            requestService.updateRequestStatusByERFX(request);

            return true;
        }
        return false;
    }

    @Override
    public String getCreateNewURL(String authCode) {
        // Generate eRFX URL for Create New Menu
        String url1 = getMasterERFXURL(authCode);
        String url2 = getActionERFXURL(CREATE_NEW.getPath());
        return url1 + url2;
    }

    @Override
    public String getCheckStatusURL(String authCode) {
        // Generate eRFX URL for Check Status Menu
        String url1 = getMasterERFXURL(authCode);
        String url2 = getActionERFXURL(CHECK_STATUS.getPath());
        return url1 + url2;
    }

    @Override
    public String getApproveShortlistURL(String authCode) {
        // Generate eRFX URL for Approve Shortlist Menu
        String url1 = getMasterERFXURL(authCode);
        String url2 = getActionERFXURL(APPROVE_SHORTLIST.getPath());
        return url1 + url2;
    }

    @Override
    public String getDraftURL(String authCode, String erfxNum) {
        // Generate eRFX URL for DRAFT mode
        String url1 = getMasterERFXURL(authCode);
        String url2 = getActionERFXURL(DRAFT.getPath(), erfxNum);
        return url1 + url2;
    }

    private String getMasterERFXURL(String authCode) {
        return String.format("%s/?cookie=%s&path=", erfxConfig.getBASEURL_PTVN_COOKIE(), authCode);
    }

    private String getActionERFXURL(String actionMenu) {
        return CommonUtils.encodeValue(String.format("%s/ep/AuthLogin.action?SelectService=%s", erfxConfig.getHostName(), actionMenu));
    }

    private String getActionERFXURL(String actionMenu, String erfxNum) {
        return CommonUtils.encodeValue(String.format("%s/ep/AuthLogin.action?SelectService=%s&erfx_id=%s", erfxConfig.getHostName(), actionMenu, erfxNum));
    }

//    private ERFXStatusResponse getERFXStatusList(ERFXStatusRequest eRFXStatusRequest, String refreshToken) {
//        String authHeader = "Bearer " + (refreshToken == null ? AppUtil.getJwtToken() : refreshToken);
//        return erfxClient.getErfxStatus(authHeader, eRFXStatusRequest);
//    }

    @Override
    public ERFXBidDocResponse getBidDoc(String docNum) {
        ERFXBidDocResponse eRFXBidDocResponse = new ERFXBidDocResponse();
        Request request = requestRepository.findFirstByRequestNo(docNum);
        if (request == null) {
            return null;
        }
        RequesterRequestDto requestDto = requestService.findRequestSourcingByRecId(request.getRecId());

        eRFXBidDocResponse.setDocNum(requestDto.getRequestNo());
        eRFXBidDocResponse.setOrganization(requestDto.getOrganizationObj() != null ? requestDto.getOrganizationObj().getName() : null);
        eRFXBidDocResponse.setDepartment(requestDto.getDepartment());
        eRFXBidDocResponse.setPhone(requestDto.getPhone());
        eRFXBidDocResponse.setMobile(requestDto.getMobile());
        eRFXBidDocResponse.setProjectCode(requestDto.getProjectCode());
        eRFXBidDocResponse.setProjectName(requestDto.getProjectName());
        eRFXBidDocResponse.setProjectLabel(requestDto.getProjectCode() != null ? requestDto.getProjectCode() + "-" + requestDto.getProjectName() : requestDto.getProjectName());
        //eRFXBidDocResponse.setProjectCode(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getName() : null);
        eRFXBidDocResponse.setCostcenter(requestDto.getCostcenter());
        eRFXBidDocResponse.setObjective(requestDto.getObjective());
        eRFXBidDocResponse.setBackground(requestDto.getBackground());
        //TODO Item ??
        List<RequestItemV2Dto> requestItemDtoList = requestItemService.getRequestItemByRequestId(requestDto.getRecId());
        this.setOptionalObjs(requestItemDtoList);

        if(requestItemDtoList != null && !requestItemDtoList.isEmpty()) {
            eRFXBidDocResponse.setCategory(requestItemDtoList.get(0).getCategoryObj() != null ? requestItemDtoList.get(0).getCategoryObj().getName() : null);
            eRFXBidDocResponse.setSubCategory(requestItemDtoList.get(0).getSubCategoryObj() != null ? requestItemDtoList.get(0).getSubCategoryObj().getName() : null);
        }

        eRFXBidDocResponse.setOutsourceService(requestDto.getOutsourceService() != null ? ("true".equals(requestDto.getOutsourceService().getValue()) ? 1 : 0) : 0);
        eRFXBidDocResponse.setOutsourceCode(requestDto.getOutsourceService() != null ? requestDto.getOutsourceService().getName() : null);
        eRFXBidDocResponse.setRequireSuppPerfEva(requestDto.getPerformanceEvaluation() != null ? ("true".equals(requestDto.getPerformanceEvaluation().getValue()) ? 1 : 0) : 0);
        eRFXBidDocResponse.setMakingContract(requestDto.getMakingContract() != null ? ("true".equals(requestDto.getMakingContract().getValue()) ? 1 : 0) : 0);
        eRFXBidDocResponse.setMakingContractReason(requestDto.getMakingContractReason());
        eRFXBidDocResponse.setMakingRptContract(requestDto.getMakingRptContract() != null ? ("true".equals(requestDto.getMakingRptContract().getValue()) ? 1 : 0) : 0);
        eRFXBidDocResponse.setMakingRptContractReason(requestDto.getMakingRptContractReason());
        eRFXBidDocResponse.setVatAbsorbedBy(0); //TODO SR is String type ***
        eRFXBidDocResponse.setStampDutyAbsorbedBy(0); //TODO SR is String type ***
        eRFXBidDocResponse.setWhtAbsorbedBy(0); //TODO SR is String type ***
        eRFXBidDocResponse.setNeedWhtCert(requestDto.getNeedWhtCert() != null ? ("true".equals(requestDto.getNeedWhtCert().getValue()) ? 1 : 0) : 0);
        eRFXBidDocResponse.setEvaluationDate(requestDto.getEvaluationDate());
        eRFXBidDocResponse.setBudgetRefNo(requestDto.getBudgetRefNo());
        eRFXBidDocResponse.setRequestedDate(requestDto.getRequestDate());
        return eRFXBidDocResponse;
    }

    @Override
    public ERFXCreateRequest testZero(ERFXCreateRequest request, String authCode) {
        if (request.getItems() == null || request.getItems().size() == 0) {
            return null;
        }
        RequestItem requestItem = requestItemRepository.findRequestItemByRecId(request.getItems().get(0).getItemId().longValue());
        RequesterRequestDto requestDto = requestService.findRequestSourcingByRecId(requestItem.getRequest().getRecId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        request.setRequesterName(requestDto.getRequester());
        request.setDepartment(requestDto.getDepartment());
        request.setPhone(requestDto.getPhone());
        request.setMobile(requestDto.getMobile());
        request.setEmail(requestDto.getEmail());
        request.setTargetDate(requestDto.getExpectedDate() != null ? DateTimeUtil.convertTimestampByUserTimeZoneStr(requestDto.getExpectedDate(), timeZone) : null);
        request.setReminder(requestDto.getRequestNo());

        List<ERFXAttachmentDto> attachments = request.getAttachments();
        setUrlAttachmentCreateERFX(attachments);

        return request;
    }

    @Override
    public ERFXAdditionalDataRequest test(ERFXCreateRequest request, String authCode) {
        if (request.getItems() == null || request.getItems().size() == 0) {
            return null;
        }
        RequestItem requestItem = requestItemRepository.findRequestItemByRecId(request.getItems().get(0).getItemId().longValue());
        RequesterRequestDto requestDto = requestService.findRequestSourcingByRecId(requestItem.getRequest().getRecId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<RequestItemV2Dto> requestItemDtoList = requestItemService.getRequestItemByRequestId(requestDto.getRecId());
        this.setOptionalObjs(requestItemDtoList);

        request.setRequesterName(requestDto.getRequester());
        request.setDepartment(requestDto.getDepartment());
        request.setPhone(requestDto.getPhone());
        request.setMobile(requestDto.getMobile());
        request.setEmail(requestDto.getEmail());
        request.setTargetDate(requestDto.getExpectedDate() != null ? DateTimeUtil.convertTimestampByUserTimeZoneStr(requestDto.getExpectedDate(), timeZone) : null);
        request.setReminder(requestDto.getRequestNo());

        List<ERFXAttachmentDto> attachments = request.getAttachments();
        setUrlAttachmentCreateERFX(attachments);
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        ERFXCreateResponse response = erfxClient.createErfx(authHeader, request);
        if (response.getItems().size() > 0) {
            ERFXAdditionalDataRequest additionalDataRequest = new ERFXAdditionalDataRequest();
            additionalDataRequest.setErfxId(response.getErfxNum());
            additionalDataRequest.setProjectCode(requestDto.getProjectCode());
            additionalDataRequest.setProjectName(requestDto.getProjectName());
            additionalDataRequest.setProjectLabel(requestDto.getProjectCode() != null ? requestDto.getProjectCode() + "-" + requestDto.getProjectName() : requestDto.getProjectName());
            //additionalDataRequest.setProjectCode(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() : null);
            additionalDataRequest.setObjective(requestDto.getObjective());
            additionalDataRequest.setOutsourceService(requestDto.getOutsourceService() != null ? ("true".equals(requestDto.getOutsourceService().getValue()) ? 1 : 0) : 0);
            additionalDataRequest.setOutsourceCode(requestDto.getOutsourceService() != null ? requestDto.getOutsourceService().getName() : null);
            additionalDataRequest.setRequireSuppPerfEva(requestDto.getPerformanceEvaluation() != null ? ("true".equals(requestDto.getPerformanceEvaluation().getValue()) ? 1 : 0) : 0);
            additionalDataRequest.setEvaluationDate(requestDto.getEvaluationDate() != null ? DateTimeUtil.convertTimestampByUserTimeZoneStr(requestDto.getEvaluationDate(), timeZone) : null);
            additionalDataRequest.setMakingContract(requestDto.getMakingContract() != null ? ("true".equals(requestDto.getMakingContract().getValue()) ? 1 : 0) : 0);
            additionalDataRequest.setMakingContractReason(requestDto.getMakingContractReason());
            additionalDataRequest.setMakingRptContract(requestDto.getMakingRptContract() != null ? ("true".equals(requestDto.getMakingRptContract().getValue()) ? 1 : 0) : 0);
            additionalDataRequest.setMakingRptContractReason(requestDto.getMakingRptContractReason());
            int vatAbsorbedBy = 0;
            switch (requestDto.getVatAbsorbedBy() != null ? requestDto.getVatAbsorbedBy().getValue() : "") {
                case "BAY & Subs.":
                    vatAbsorbedBy = 1;
                    break;
                case "Vendors":
                    vatAbsorbedBy = 2;
                    break;
                default:
                    break;
            }
            additionalDataRequest.setVatAbsorbedBy(vatAbsorbedBy);
            int stampDutyAbsorbedBy = 0;
            switch (requestDto.getStampDuty() != null ? requestDto.getStampDuty().getValue() : "") {
                case "BAY & Subs.":
                    stampDutyAbsorbedBy = 1;
                    break;
                case "Vendors":
                    stampDutyAbsorbedBy = 2;
                    break;
                default:
                    break;
            }
            additionalDataRequest.setStampDutyAbsorbedBy(stampDutyAbsorbedBy);
            int whtAbsorbedBy = 0;
            switch (requestDto.getWhtAbsorbedBy() != null ? requestDto.getWhtAbsorbedBy().getValue() : "") {
                case "BAY & Subs.":
                    whtAbsorbedBy = 1;
                    break;
                case "Vendors":
                    whtAbsorbedBy = 2;
                    break;
                case "No":
                    whtAbsorbedBy = 3;
                    break;
                case "Not Specific":
                    whtAbsorbedBy = 4;
                    break;
                default:
                    break;
            }
            additionalDataRequest.setWhtAbsorbedBy(whtAbsorbedBy);
            additionalDataRequest.setNeedWhtCert(requestDto.getNeedWhtCert() != null ? ("true".equals(requestDto.getNeedWhtCert().getValue()) ? 1 : 0) : 0);
            List<ERFXAdditionalDataItemDto> items = new ArrayList<>();
            for (ERFXItemDto requestItemBody : request.getItems()) {
                RequestItemV2Dto requestItemDto = requestItemDtoList.stream().filter(requestItemList -> requestItemList.getRecId() == requestItemBody.getItemId().intValue()).findFirst().get();
                ERFXAdditionalDataItemDto item = new ERFXAdditionalDataItemDto();
                ERFXItemResponse erfxItem = response.getItems().stream().filter(erfxItemResponse -> erfxItemResponse.getItemId() == requestItemDto.getRecId().intValue()).findFirst().get();
                item.setItemId(erfxItem.getErfxItemId());
                item.setBidValidityStartDate(requestItemDto.getBidValidityStartDate() != null ? requestItemDto.getBidValidityStartDate() : null);
                item.setBidValidityEndDate(requestItemDto.getBidValidityEndDate() != null ? requestItemDto.getBidValidityEndDate() : null);
                item.setCategory(requestItemDto.getCategoryObj() != null ? requestItemDto.getCategoryObj().getName() : null);
                item.setSubCategory(requestItemDto.getSubCategoryObj() != null ? requestItemDto.getSubCategoryObj().getName() : null);
                items.add(item);
            }
            additionalDataRequest.setItems(items);

            List<ERFXQuestionnaireQuestionRequest> questions = new ArrayList<>();
            if (requestDto.getQuestionnaire() != null) {
                if (requestDto.getQuestionnaire().getFormTabs() != null && requestDto.getQuestionnaire().getFormTabs().size() > 0) {
                    for (FormTabDTO formTabDTO : requestDto.getQuestionnaire().getFormTabs()) {
                        if (formTabDTO.getFormSections() != null && formTabDTO.getFormSections().size() > 0) {
                            for (FormSectionDTO formSectionDTO : formTabDTO.getFormSections()) {
                                if (formSectionDTO.getFormFields() != null && formSectionDTO.getFormFields().size() > 0) {
                                    for (FormFieldDTO formFieldDTO : formSectionDTO.getFormFields()) {
                                        ERFXQuestionnaireQuestionRequest question = new ERFXQuestionnaireQuestionRequest();
                                        question.setQuestion(formFieldDTO.getFieldName());
                                        String questionType = "";
                                        switch (formFieldDTO.getFieldTypeName()) {
                                            case "Short Answer":
                                                questionType = "text_box";
                                                break;
                                            case "Numeric":
                                                questionType = "num_text_box";
                                                break;
                                            case "Checkboxes":
                                                questionType = "check_box";
                                                break;
                                            case "Multiple Choice":
                                                questionType = "multiple_choice";
                                                break;
                                            case "File Upload":
                                                questionType = "doc_required";
                                                break;
                                            default:
                                                questionType = "text_box";
                                                break;
                                        }
                                        question.setQuestionType(questionType);
                                        question.setQuestionHelp(formFieldDTO.getFieldDesc());
                                        question.setQuestionWeight(formFieldDTO.getWeight().intValue() != 0 ? formFieldDTO.getWeight().intValue() : null);
                                        question.setQuestionOrder(formFieldDTO.getSequence());
                                        question.setRequireField(formFieldDTO.getIsRequire() != null && formFieldDTO.getIsRequire() ? "Y" : "N");
                                        question.setOther("N");//TODO
                                        List<ERFXQuestionnaireAnswerRequest> answers = new ArrayList<>();
                                        if (formFieldDTO.getOptionChoices() != null && formFieldDTO.getOptionChoices().size() > 0) {
                                            for (FormOptionChoiceDTO formOptionChoiceDTO : formFieldDTO.getOptionChoices()) {
                                                ERFXQuestionnaireAnswerRequest answer = new ERFXQuestionnaireAnswerRequest();
                                                answer.setAnswer(formOptionChoiceDTO.getChoiceName());
                                                answer.setAnswerHelp(null);//TODO
                                                answer.setAnswerPoint(formOptionChoiceDTO.getPoint().intValue() != 0 ? formOptionChoiceDTO.getPoint().intValue() : null);
                                                answer.setAnswerOrder(formOptionChoiceDTO.getSequence());
                                                answers.add(answer);
                                            }
                                        }
                                        question.setAnswers(answers);
                                        questions.add(question);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            additionalDataRequest.setQuestionnaire(questions);
            return additionalDataRequest;
        }
        return null;
    }

    private void setOptionalObjs(List<RequestItemV2Dto> requestItemDtoList) {

//        Tenant tenant = tenantService.findByCode(tenantCode);
//        Map<String, String> buyerFullName = new HashMap<>();
//        if (null != tenant) {
//            if(requestDto.getTypeObj() != null) {
//                List<TenantSubCategory> subCategories = tenantSubCategoryRepository.getSubcategoryByTenantIdAndTypeId(tenant.getRecId(), Integer.parseInt(requestDto.getTypeObj().getValue()), "");
//                if(subCategories != null) {
//                    List<String> buyerNames = subCategories.stream().map(TenantSubCategory::getBuyer).distinct().collect(Collectors.toList());
//                    for (String buyer : buyerNames) {
//                        if (buyer != null && !buyer.trim().isEmpty()) {
//                            buyerFullName.put(buyer, UserDetailServiceUtil.getFullName(buyer));
//                        }
//                    }
//                }
//            }
//        }

        for (RequestItemV2Dto requestItemDto : requestItemDtoList) {

            Optional<RequestItemSubCategory> requestItemSubCategoryOptional = requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
            if (requestItemSubCategoryOptional.isPresent()) {
                TenantSubCategoryDto tenantSubCategoryDto = tenantSubCategoryService.findById(requestItemSubCategoryOptional.get().getSubCategory().getId());
                if (null != tenantSubCategoryDto) {
//                    requestItemDto.setPurchaser(tenantSubCategoryDto.getBuyer());
//                    requestItemDto.setDisplayPurchaser(null != buyerFullName.get(tenantSubCategoryDto.getBuyer()) ? buyerFullName.get(tenantSubCategoryDto.getBuyer()) : "");
                    requestItemDto.setSubCategoryObj(tenantSubCategoryDto);
                    requestItemSubCategoryOptional.ifPresent(requestItemSubCategory -> requestItemDto.setCategoryObj(mapOptionalToOptionDto(Optional.of(requestItemSubCategory.getSubCategory().getCategory()), OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto)));
                }
            }
        }
    }
}
