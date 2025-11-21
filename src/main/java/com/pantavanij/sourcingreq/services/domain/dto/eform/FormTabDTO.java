package com.pantavanij.sourcingreq.services.domain.dto.eform;

import lombok.Data;

import java.util.List;

@Data
public class FormTabDTO {
    private Long tabId;
    private Integer sequence;
    private String tabName;
    private String tabDesc;
    private Boolean isShowTab;
    private List<FormSectionDTO> formSections;
}
