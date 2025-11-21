package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class ERFXCreateAllRequest {
    private ERFXCreateRequest createRequest;
    private ERFXAdditionalDataRequest additionalDataRequest;
    private ERFXBidDocRequest bidDocRequest;
    private ERFXRequesterInformationRequest requesterInformationRequest;
    private ERFXQuestionnaireRequest questionnaireSectionRequest;
}