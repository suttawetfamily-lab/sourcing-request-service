package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestQuestionnaireRequest {
    @NotNull(message = "requestId must not be null")
    private Long requestId;
    private Long formId;
    private Long duplicateFromRequestId;
}
