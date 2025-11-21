package com.pantavanij.sourcingreq.services.domain.dto.eform;

import lombok.Data;

import java.util.List;

@Data
public class FormSectionDTO {
    private Long sectionId;
    private String sectionName;
    private String sectionDesc;
    private Integer sequence;
    private Boolean isShowSection;
    private List<FormFieldDTO> formFields;
}
