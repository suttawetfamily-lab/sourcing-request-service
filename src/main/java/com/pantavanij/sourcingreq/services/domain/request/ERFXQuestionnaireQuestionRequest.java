package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class ERFXQuestionnaireQuestionRequest {
    private String question;
    private String questionType;
    private String questionHelp;
    private Integer questionWeight;
    private Integer questionOrder;
    private String requireField;
    private String other;
    private List<ERFXQuestionnaireAnswerRequest> answers;
}