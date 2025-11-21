package com.pantavanij.sourcingreq.services.domain.response.eform;

import com.pantavanij.sourcingreq.services.domain.dto.eform.QuestionnaireSearchDTO;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import lombok.Data;

@Data
public class EFormSearchResponse {

    private ApiResponseStatus status;

    private QuestionnaireSearchDTO data;

}
