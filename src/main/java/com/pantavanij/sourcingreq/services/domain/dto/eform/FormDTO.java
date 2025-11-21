package com.pantavanij.sourcingreq.services.domain.dto.eform;

import lombok.Data;

import javax.persistence.Column;
import java.util.List;

@Data
public class FormDTO {
    private String projectId = "Sourcing Request";
    private String tenantId = "bay";
    private Long formId;
    private String formName;
    private String formType;
    private String formDesc;
    private String formNumber;
    private String status;
    private Boolean active;
    private Long parentFormId = 0L;
    private Double passScore = 0.0;
    private String resultType = "Weight";
    private Double totalScore = 0.0;
    private Integer versionNumber;
    private List<FormTabDTO> formTabs;
    private Double totalResultScore = 0.0;
    private String createdDate;
    private String createdBy;
    private String createdUsername;
    private String updatedDate;
    private String updatedBy;
    private String updatedUsername;
    private String formCreatedBy;
    private String formUpdatedBy;
    private String formCreatedDate;
    private String formUpdatedDate;
    private Boolean isMaster;

}
