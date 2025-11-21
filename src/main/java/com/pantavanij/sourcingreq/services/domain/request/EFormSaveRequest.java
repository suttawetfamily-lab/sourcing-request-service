package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormTabDTO;
import lombok.Data;

import java.util.List;

@Data
public class EFormSaveRequest {
    private String projectId;
    private String tenantId;
    private Long formId;
    @JsonProperty(value = "questionnaireTemplateName")
    private String formName;
    private String formType;
    @JsonProperty(value = "questionnaireDescription")
    private String formDesc;
    private String formNumber;
    private String status;
    private Boolean active;
    private Long parentFormId;
    private Double passScore;
    private String resultType;
    private Double totalScore;
    private Integer versionNumber;
    private List<FormTabDTO> formTabs;
    private Double totalResultScore;
    private String createdDate;
    private String createdBy;
    private String updatedDate;
    private String updatedBy;
}
