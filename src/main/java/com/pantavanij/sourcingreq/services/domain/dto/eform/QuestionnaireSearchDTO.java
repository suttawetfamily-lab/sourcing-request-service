package com.pantavanij.sourcingreq.services.domain.dto.eform;

import lombok.Data;

import java.util.List;
@Data
public class QuestionnaireSearchDTO {
    private List<QuestionnaireTemplateDTO> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
