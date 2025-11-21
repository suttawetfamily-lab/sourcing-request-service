package com.pantavanij.sourcingreq.services.domain.response.eform;

import com.pantavanij.sourcingreq.services.domain.dto.eform.FormDTO;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import lombok.Data;

@Data
public class DuplicateQuestionnaireResponse {
    private ApiResponseStatus status;
    private Long data;
}
