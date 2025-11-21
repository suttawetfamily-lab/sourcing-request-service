package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EFormClient;
import com.pantavanij.sourcingreq.services.domain.dto.eform.QuestionnaireTemplateDTO;
import com.pantavanij.sourcingreq.services.domain.request.DuplicateQuestionnaireRequest;
import com.pantavanij.sourcingreq.services.domain.request.EFormSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.eform.DuplicateQuestionnaireResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormSearchResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormViewResponse;
import com.pantavanij.sourcingreq.services.service.sourcingreq.EFormService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Slf4j
@RequiredArgsConstructor
@Service
public class EFormServiceImpl implements EFormService {

    private final EFormClient eFormClient;

    @Override
    public EFormSearchResponse searchQuestionnaireTemplate(EFormSearchRequest request) {
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        EFormSearchResponse eFormSearchResponse = eFormClient.searchQuestionnaire(authHeader, request);
        eFormSearchResponse.getData().getData().sort(Comparator.comparing(QuestionnaireTemplateDTO::getUpdatedDate, Comparator.reverseOrder()));
        return eFormSearchResponse;
    }

    @Override
    public EFormViewResponse viewQuestionnaire(Long formId) {
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        return eFormClient.viewQuestionnaire(authHeader, formId);
    }

    @Override
    public EFormViewResponse deleteQuestionnaire(Long formId, Boolean isNotCheckStatusDraft) {
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        return eFormClient.deleteQuestionnaire(authHeader, formId, isNotCheckStatusDraft);
    }

    @Override
    public DuplicateQuestionnaireResponse duplicateSourcingRequestForm(Long formId, Long requestId) {
        try {
            String authHeader = "Bearer " + AppUtil.getJwtToken();
            DuplicateQuestionnaireRequest request = new DuplicateQuestionnaireRequest();
            request.setRequestId(requestId);

            DuplicateQuestionnaireResponse response =
                    eFormClient.duplicateSourcingRequestForm(authHeader, formId, request);

            return response;
        } catch (Exception ex) {
            log.error("Call duplicateSourcingRequestForm failed: {}", ex.getMessage(), ex);
            throw new RuntimeException("Cannot duplicate sourcing request form", ex);
        }
    }

}
