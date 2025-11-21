package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.projection.RequestIdStatusProjection;
import com.pantavanij.sourcingreq.services.domain.request.EFormSaveRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestQuestionnaireRequest;
import com.pantavanij.sourcingreq.services.domain.request.UpdateFormIdRequest;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormViewResponse;

import java.util.List;

public interface RequestQuestionnaireService {
    Long save(RequestQuestionnaireRequest request) throws CloneNotSupportedException;
    void updateThisQuestionnaire(EFormSaveRequest request, Long requestId);
    EFormViewResponse getEFormView(Long formId, Long requestId, Integer isDeselect);
    void updateRequestQuestionnaireFormId(Long requestId, UpdateFormIdRequest request);
    Long getFormIdByRequestId(Long requestId);
    List<RequestIdStatusProjection> getRequestIdsByFormIdWithStatus(Long formId, List<Integer> notAllowedStatus);
    boolean isFormInUse(Long formId);
}