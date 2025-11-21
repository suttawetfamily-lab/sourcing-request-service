package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class ERFXQuestionnaireRequest {
    private List<ERFXQuestionnaireQuestionRequest> questions;
}