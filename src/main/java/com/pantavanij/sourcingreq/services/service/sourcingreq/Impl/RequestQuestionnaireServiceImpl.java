package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.eform.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.QuestionnaireMapper;
import com.pantavanij.sourcingreq.services.domain.projection.RequestIdStatusProjection;
import com.pantavanij.sourcingreq.services.domain.request.DelegationActiveRequest;
import com.pantavanij.sourcingreq.services.domain.request.EFormSaveRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestQuestionnaireRequest;
import com.pantavanij.sourcingreq.services.domain.request.UpdateFormIdRequest;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.DuplicateQuestionnaireResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormViewResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.AppException;
import com.pantavanij.sourcingreq.services.exception.DataNotFoundException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_QUESTIONNAIRE;
import static com.pantavanij.sourcingreq.services.enums.RequestStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestQuestionnaireServiceImpl implements RequestQuestionnaireService {

    private final RequestQuestionnaireRepository requestQuestionnaireRepository;
    private final RequestRepository requestRepository;
    private final RequestQuestionnaireFormFieldRepository requestQuestionnaireFormFieldRepository;
    private final RequestQuestionnaireFormFieldOptionChoiceRepository requestQuestionnaireFormFieldOptionChoiceRepository;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final EFormService eFormService;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final DelegationService delegationService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(RequestQuestionnaireRequest req) throws CloneNotSupportedException {
        log.info("save RequestQuestionnaire with requestId {}, formId {}", req.getRequestId(), req.getFormId());
        Long result = null;
        boolean isSameForm = false;


        UserDto user = AppUtil.getUser();
        Request request_ = requestRepository.findRequestsByRecId(req.getRequestId());
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

        if (AppUtil.isAllowedAction(user, request_, requestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_QUESTIONNAIRE)) {
            Optional<RequestQuestionnaire> requestQuestionnaireOptional = requestQuestionnaireRepository.findByRequestId(req.getRequestId());
            RequestQuestionnaire requestQuestionnaire = null;
            if (requestQuestionnaireOptional.isPresent()) {
                // Case update, change questionnaire template
                requestQuestionnaire = requestQuestionnaireOptional.get();
                if (null != req.getFormId()) {
                    log.info("Update RequestQuestionnaire with requestId {}, formId {}", req.getRequestId(), req.getFormId());

//                    if (Objects.equals(requestQuestionnaire.getFormId(), req.getFormId())) {
//                        isSameForm = true;
//                    }
                    requestQuestionnaire.setFormId(req.getFormId());
                    requestQuestionnaire.setUpdatedBy(AppUtil.getUserName());
                    requestQuestionnaire.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    requestQuestionnaire = requestQuestionnaireRepository.save(requestQuestionnaire);

//                    if (!isSameForm) {
//                        cleanUpFormField(requestQuestionnaire.getRecId());
//                        EFormViewResponse eFormViewResponse = eFormService.viewQuestionnaire(requestQuestionnaire.getFormId());
//                        if (null != eFormViewResponse) {
//                            insertFormField(eFormViewResponse.getData(), requestQuestionnaire);
//                        }
//                    }

                    result = requestQuestionnaire.getRecId();
                } else {
                    log.info("[DELETE] Deselect RequestQuestionnaire with requestId {}", req.getRequestId());
                    // deselect questionnaire template
//                    cleanUpFormField(requestQuestionnaire.getRecId());
                    requestQuestionnaireRepository.delete(requestQuestionnaire);
                    result = -1L;
                }
            } else {
                Request request = requestRepository.findRequestsByRecId(req.getRequestId());
                if (null != req.getDuplicateFromRequestId()) {
                    Optional<RequestQuestionnaire> dupRequestQuestionnaireOptional = requestQuestionnaireRepository.findByRequestId(req.getDuplicateFromRequestId());
                    if (dupRequestQuestionnaireOptional.isPresent()) {
                        RequestQuestionnaire cloneRequestQuestionnaire = (RequestQuestionnaire) dupRequestQuestionnaireOptional.get().clone();
                        cloneRequestQuestionnaire.setRecId(null);
//                        cloneRequestQuestionnaire.setRequestQuestionnaireFormFields(null);
                        cloneRequestQuestionnaire.setRequest(request);
                        requestQuestionnaireRepository.save(cloneRequestQuestionnaire);

//                        List<RequestQuestionnaireFormField> cloneFormField = dupRequestQuestionnaireOptional.get().getRequestQuestionnaireFormFields();
//                        cloneFormField.stream().forEach(field -> {
//                            RequestQuestionnaireFormField newField = null;
//                            try {
//                                newField = (RequestQuestionnaireFormField) field.clone();
//                                newField.setRecId(null);
//                                newField.setRequestQuestionnaireFormFieldOptionChoices(null);
//                                newField.setRequestQuestionnaire(cloneRequestQuestionnaire);
//                            } catch (CloneNotSupportedException e) {
//                                throw new RuntimeException(e);
//                            }
//                            requestQuestionnaireFormFieldRepository.save(newField);
//
//                            final RequestQuestionnaireFormField finalNewField = newField;
//                            if (field.getRequestQuestionnaireFormFieldOptionChoices() != null && field.getRequestQuestionnaireFormFieldOptionChoices().size() > 0) {
//                                field.getRequestQuestionnaireFormFieldOptionChoices().stream()
//                                    .forEach(opt -> {
//                                        RequestQuestionnaireFormFieldOptionChoice optionChoice = null;
//                                        try {
//                                            optionChoice = (RequestQuestionnaireFormFieldOptionChoice) opt.clone();
//                                            optionChoice.setRecId(null);
//                                            optionChoice.setRequestQuestionnaireFormField(finalNewField);
//                                        } catch (CloneNotSupportedException e) {
//                                            throw new RuntimeException(e);
//                                        }
//                                        requestQuestionnaireFormFieldOptionChoiceRepository.save(optionChoice);
//                                    });
//                            }
//                        });
                        result = cloneRequestQuestionnaire.getRecId();

                    } else {
                        result = -1L;
                    }

                // Case insert //
                } else if (null != req.getFormId() && null != request) {
                    log.info("Add RequestQuestionnaire with requestId {}, formId {}", req.getRequestId(), req.getFormId());
                    requestQuestionnaire = RequestQuestionnaire.builder()
                            .request(request)
                            .formId(req.getFormId())
                            .createdBy(AppUtil.getUserName())
                            .createdDate(DateTimeUtil.getTimestampUTC())
                            .build();

                    requestQuestionnaire = requestQuestionnaireRepository.save(requestQuestionnaire);

                    // insert questionnaire form field
//                    EFormViewResponse eFormViewResponse = eFormService.viewQuestionnaire(requestQuestionnaire.getFormId());
//                    if (null != eFormViewResponse) {
//                        insertFormField(eFormViewResponse.getData(), requestQuestionnaire);
//                    }

                    result = requestQuestionnaire.getRecId();
                } else {
                    log.info("Nothing to save RequestQuestionnaire with requestId {}, formId {}", req.getRequestId(), req.getFormId());
                    result = -1L;
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateThisQuestionnaire(EFormSaveRequest request, Long requestId) {
        log.info("Update this RequestQuestionnaire with requestId {}", requestId);
        Request requestEntity = requestRepository.findRequestsByRecId(requestId);
        log.info("Update this RequestQuestionnaire with body : {}", request);

        UserDto user = AppUtil.getUser();
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(requestEntity.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(requestEntity.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        if (AppUtil.isAllowedAction(user, requestEntity, requestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_QUESTIONNAIRE)){
            RequestQuestionnaire firstEdition = requestQuestionnaireRepository.findByRequestId(requestEntity.getRecId()).get();
            String firstCreatedBy = firstEdition.getCreatedBy();
            Timestamp firstCreatedDate = firstEdition.getCreatedDate();

            // Delete existing questionnaire template
            requestQuestionnaireRepository.deleteByRequest(requestEntity);

            log.info("Add RequestQuestionnaire with requestId {}, formId {}", requestId, request.getFormId());
            RequestQuestionnaire requestQuestionnaire = RequestQuestionnaire.builder()
                    .request(requestEntity)
                    .formId(request.getFormId())
                    .createdBy(firstCreatedBy)
                    .createdDate(firstCreatedDate)
                    .updatedBy(AppUtil.getUserName())
                    .updatedDate(DateTimeUtil.getTimestampUTC())
                    //.formCreatedBy(firstCreatedBy)
                    //.formCreatedDate(firstCreatedDate.toString())
                    //.formUpdatedBy(AppUtil.getUserName())
                    //.formUpdatedDate(DateTimeUtil.getTimestamp().toString())
                    .build();

            requestQuestionnaire = requestQuestionnaireRepository.save(requestQuestionnaire);
            // insert questionnaire form field
            insertFormField(QuestionnaireMapper.INSTANCE.toFormDTO(request), requestQuestionnaire);
        } else {
            throw new AppException(ApiMessage.E7082, String.format(ApiMessage.E7082.description(), HttpStatus.METHOD_NOT_ALLOWED));
        }
    }

    @Override
    public EFormViewResponse getEFormView(Long formId, Long requestId, Integer isDeSelect) {
        log.info("Get EFormView for FormId : {} and RequestId : {}", formId, requestId);
        UserDto user = AppUtil.getUser();
        EFormViewResponse eFormViewResponse = new EFormViewResponse();
        if (requestId == null) {
            log.info("Call EForm to Get {} detail.", formId);
            eFormViewResponse = eFormService.viewQuestionnaire(formId);
        } else {
            log.info("Get EForm from SourcingDB with RequestId : {} and FormId : {}", requestId, formId);
            try {
                RequestQuestionnaire requestQuestionnaire = requestQuestionnaireRepository.findByRequestId(requestId)
                        .orElseThrow(() -> new DataNotFoundException("Cannot get RequestQuestionnaire for RequestId : " + requestId + ", FormId : " + formId));
                if ((!Objects.equals(requestQuestionnaire.getFormId(), formId)) || (Objects.equals(requestQuestionnaire.getFormId(), formId) && isDeSelect == 1)) {
                    log.info("Call EForm to Get new form id {} detail.", formId);
                    eFormViewResponse = eFormService.viewQuestionnaire(formId);
                    String createdBy = eFormViewResponse.getData().getCreatedUsername();
                    String updatedUsername = eFormViewResponse.getData().getUpdatedUsername();
                    eFormViewResponse.getData().setCreatedBy(UserDetailServiceUtil.getFullName(createdBy));
                    String updatedBy = UserDetailServiceUtil.getFullName(updatedUsername);
                    eFormViewResponse.getData().setUpdatedBy((updatedBy != null && !updatedBy.isEmpty()) ? updatedBy : updatedUsername);
                } else {
                    log.info("Get RequestQuestionnaire RecId : {}", requestQuestionnaire.getRecId());
                    //eFormViewResponse.setData(setEFormViewData(requestQuestionnaire));
                    eFormViewResponse = eFormService.viewQuestionnaire(formId);
//                    FormDTO formDTO = eFormViewResponse.getData();
                    String createdBy = eFormViewResponse.getData().getCreatedBy();
                    String updatedUsername = eFormViewResponse.getData().getUpdatedBy() == null ? eFormViewResponse.getData().getCreatedBy() : eFormViewResponse.getData().getUpdatedBy();

                    if (eFormViewResponse.getData().getUpdatedDate() == null) {
                        eFormViewResponse.getData().setUpdatedDate(eFormViewResponse.getData().getCreatedDate());
                    }

                    eFormViewResponse.getData().setCreatedBy(UserDetailServiceUtil.getFullName(createdBy));
                    String updatedBy = UserDetailServiceUtil.getFullName(updatedUsername);
                    eFormViewResponse.getData().setUpdatedBy((updatedBy != null && !updatedBy.isEmpty()) ? updatedBy : updatedUsername);
                }
            } catch (DataNotFoundException exception) {
                log.info("Cannot get RequestQuestionnaire for RequestId : {}, FormId : {}", requestId, formId);
                log.info("Call EForm to Get {} detail.", formId);
                eFormViewResponse = eFormService.viewQuestionnaire(formId);
                String createdBy = eFormViewResponse.getData().getCreatedUsername();
                String updatedUsername = eFormViewResponse.getData().getUpdatedUsername();
                eFormViewResponse.getData().setCreatedBy(UserDetailServiceUtil.getFullName(createdBy));
                String updatedBy = UserDetailServiceUtil.getFullName(updatedUsername);
                eFormViewResponse.getData().setUpdatedBy((updatedBy != null && !updatedBy.isEmpty()) ? updatedBy : updatedUsername);
            }
        }
        return eFormViewResponse;
    }

    private FormDTO setEFormViewData(RequestQuestionnaire requestQuestionnaire) {
        FormDTO formDTO = QuestionnaireMapper.INSTANCE.toFormDTO(requestQuestionnaire);
        List<FormFieldDTO> fieldDTOList = new ArrayList<>();
        for(RequestQuestionnaireFormField field : requestQuestionnaire.getRequestQuestionnaireFormFields()) {
            FormFieldDTO fieldDTO = QuestionnaireMapper.INSTANCE.toFormFieldDTO(field);
            fieldDTO.setRequestQuestionnaireFormFieldId(field.getRecId());
            List<FormOptionChoiceDTO> optionChoiceDTOList = new ArrayList<>();
            for(RequestQuestionnaireFormFieldOptionChoice optionChoice : field.getRequestQuestionnaireFormFieldOptionChoices()) {
                FormOptionChoiceDTO optionChoiceDTO = QuestionnaireMapper.INSTANCE.toFormOptionChoiceDTO(optionChoice);
                optionChoiceDTOList.add(optionChoiceDTO);
            }
            fieldDTO.setOptionChoices(optionChoiceDTOList);
            fieldDTOList.add(fieldDTO);
        }
        FormTabDTO tabDTO = new FormTabDTO();
        tabDTO.setSequence(1);
        tabDTO.setIsShowTab(false);

        FormSectionDTO sectionDTO = new FormSectionDTO();
        sectionDTO.setSequence(1);
        sectionDTO.setIsShowSection(false);
        sectionDTO.setFormFields(fieldDTOList);

        tabDTO.setFormSections(Arrays.asList(sectionDTO));
        formDTO.setFormTabs(Arrays.asList(tabDTO));
//        formDTO.setFormCreatedBy(requestQuestionnaire.getFormCreatedBy());
//        formDTO.setFormUpdatedBy(requestQuestionnaire.getFormUpdatedBy());
//        formDTO.setFormCreatedDate(requestQuestionnaire.getFormCreatedDate());
//        formDTO.setFormUpdatedDate(requestQuestionnaire.getFormUpdatedDate());
        return formDTO;
    }

    private void cleanUpFormField(Long requestQuestionnaireId) {
        List<RequestQuestionnaireFormField> requestQuestionnaireFormFieldList =  requestQuestionnaireFormFieldRepository.findByRequestQuestionnaireId(requestQuestionnaireId);
        if (!requestQuestionnaireFormFieldList.isEmpty()) {
            List<Long> ids = requestQuestionnaireFormFieldList.stream().map(RequestQuestionnaireFormField::getRecId).collect(Collectors.toList());

            List<RequestQuestionnaireFormFieldOptionChoice> choiceList =  requestQuestionnaireFormFieldOptionChoiceRepository.findByRequestQuestionnaireFormField(ids);
            if (!choiceList.isEmpty()) {
                List<Long> choiceIds = choiceList.stream().map(RequestQuestionnaireFormFieldOptionChoice::getRecId).collect(Collectors.toList());
                requestQuestionnaireFormFieldOptionChoiceRepository.deleteAllById(choiceIds);
            }

            requestQuestionnaireFormFieldRepository.deleteAllById(ids);
        }
    }

    private void insertFormField(FormDTO formDTO, RequestQuestionnaire requestQuestionnaire) {
        try {
            if (!formDTO.getFormTabs().isEmpty()) {
                for (FormTabDTO formTabDTO : formDTO.getFormTabs()) {
                    if (!formTabDTO.getFormSections().isEmpty()) {
                        for (FormSectionDTO formSectionDTO : formTabDTO.getFormSections()) {
                            if (!formSectionDTO.getFormFields().isEmpty()) {

                                for (FormFieldDTO formField : formSectionDTO.getFormFields()) {
                                    RequestQuestionnaireFormField requestQuestionnaireFormField = RequestQuestionnaireFormField.builder()
                                            .requestQuestionnaire(requestQuestionnaire)
                                            .fieldId(null == formField.getFieldId() ? generateFormId() : formField.getFieldId())
                                            .fieldTypeId(formField.getFieldTypeId())
                                            .sequence(formField.getSequence())
                                            .fieldName(formField.getFieldName())
                                            .fieldDesc(formField.getFieldDesc())
                                            .defaultValue(formField.getDefaultValue())
                                            .isQuiz(formField.getIsQuiz())
                                            .isRankByResponse(formField.getIsRankByResponse())
                                            .isRequire(formField.getIsRequire())
                                            .isScore(formField.getIsScore())
                                            .maximumFile(formField.getMaximumFile())
                                            .weight(formField.getWeight())
                                            .isDisplayComment(formField.getIsDisplayComment())
                                            .fullScore(formField.getFullScore())
                                            .fieldTypeName(formField.getFieldTypeName())
                                            .createdBy(AppUtil.getUserName())
                                            .createdDate(DateTimeUtil.getTimestampUTC())
                                            .build();
                                    requestQuestionnaireFormField = requestQuestionnaireFormFieldRepository.save(requestQuestionnaireFormField);

                                    if (!formField.getOptionChoices().isEmpty()) {
                                        RequestQuestionnaireFormField finalRequestQuestionnaireFormField = requestQuestionnaireFormField;
                                        List<RequestQuestionnaireFormFieldOptionChoice> choices = formField.getOptionChoices().stream().map(i -> RequestQuestionnaireFormFieldOptionChoice.builder()
                                                .requestQuestionnaireFormField(finalRequestQuestionnaireFormField)
                                                .optionChoiceId(null == i.getOptionChoiceId() ? generateFormId() : i.getOptionChoiceId())
                                                .sequence(i.getSequence())
                                                .choiceName(i.getChoiceName())
                                                .isOther(i.getIsOther() == null ? false : i.getIsOther())
                                                .isSelected(i.getIsSelected() == null ? false : i.getIsSelected())
                                                .point(i.getPoint())
                                                .createdBy(AppUtil.getUserName())
                                                .createdDate(DateTimeUtil.getTimestampUTC())
                                                .build()).collect(Collectors.toList());
                                        requestQuestionnaireFormFieldOptionChoiceRepository.saveAll(choices);
                                    }
                                }
                            }
                        }
                    }
                }
            }

//            requestQuestionnaire.setActive(formDTO.getActive());
//            requestQuestionnaire.setFormNumber(formDTO.getFormNumber());
//            requestQuestionnaire.setFormName(formDTO.getFormName());
//            requestQuestionnaire.setFormDesc(formDTO.getFormDesc());
//            requestQuestionnaire.setVersionNumber(formDTO.getVersionNumber());
//            requestQuestionnaire.setStatus(formDTO.getStatus());
//            requestQuestionnaire.setFormCreatedBy(formDTO.getCreatedBy());
//            requestQuestionnaire.setFormCreatedDate(formDTO.getCreatedDate());
//            requestQuestionnaire.setFormUpdatedBy(formDTO.getUpdatedBy());
//            requestQuestionnaire.setFormUpdatedDate(formDTO.getUpdatedDate());
//            requestQuestionnaire.setFormType(formDTO.getFormType());

            requestQuestionnaireRepository.save(requestQuestionnaire);
        } catch (Exception ex) {
            throw new AppException(ApiMessage.E7076, String.format(ApiMessage.E7076.description(), ex.getMessage()));
        }
    }

    private Long generateFormId() {
        return RandomUtils.nextLong(1L, 1000000L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRequestQuestionnaireFormId(Long requestId, UpdateFormIdRequest request) {
        Optional<RequestQuestionnaire> rqOpt = requestQuestionnaireRepository.findByRequestId(requestId);

        if (!rqOpt.isPresent()) {
            throw new AppException(ApiMessage.E7010,
                    String.format(ApiMessage.E7010.description(), HttpStatus.NOT_FOUND));
        }

        RequestQuestionnaire rq = rqOpt.get();

        rq.setFormId(request.getFormId());
        rq.setUpdatedBy(AppUtil.getUserName());
        rq.setUpdatedDate(DateTimeUtil.getTimestampUTC());

        requestQuestionnaireRepository.save(rq);
    }

    @Override
    public Long getFormIdByRequestId(Long requestId) {
        Optional<RequestQuestionnaire> rqOpt = requestQuestionnaireRepository.findByRequestId(requestId);

        if (!rqOpt.isPresent()) {
            throw new AppException(ApiMessage.E7082,
                    String.format(ApiMessage.E7082.description(), HttpStatus.NOT_FOUND));
        }

        RequestQuestionnaire rq = rqOpt.get();
        return rq.getFormId();
    }

    public List<RequestIdStatusProjection> getRequestIdsByFormIdWithStatus(Long formId, List<Integer> notAllowedStatus) {
//        List<Integer> notAllowedStatus = Arrays.asList(
//                REQUEST_AWAITING.id(),
//                REQUEST_PARTIAL_COMPLETED.id(),
//                REQUEST_COMPLETED.id(),
//                REQUEST_REJECTED.id(),
//                REQUEST_CANCELLED.id(),
//                REQUEST_PENDING.id()
//        );
        return requestQuestionnaireRepository.findRequestIdAndStatusByFormIdAndStatusId(formId, notAllowedStatus);
    }

    public boolean isFormInUse(Long formId) {
        return requestQuestionnaireRepository.existsByFormId(formId);
    }

}
