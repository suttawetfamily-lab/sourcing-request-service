package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.request.DuplicateQuestionnaireRequest;
import com.pantavanij.sourcingreq.services.domain.request.EFormSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.eform.DuplicateQuestionnaireResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormSearchResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormViewResponse;

public interface EFormService {

    EFormSearchResponse searchQuestionnaireTemplate(EFormSearchRequest request);

    EFormViewResponse viewQuestionnaire(Long formId);

    EFormViewResponse deleteQuestionnaire(Long formId, Boolean isNotCheckStatusDraft);

    DuplicateQuestionnaireResponse duplicateSourcingRequestForm(Long formId, Long requestId);
}
