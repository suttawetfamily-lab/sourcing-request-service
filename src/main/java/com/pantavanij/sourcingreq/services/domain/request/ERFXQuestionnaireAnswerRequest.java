package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class ERFXQuestionnaireAnswerRequest {
    private String answer;
    private String answerHelp;
    private Integer answerPoint;
    private Integer answerOrder;
}