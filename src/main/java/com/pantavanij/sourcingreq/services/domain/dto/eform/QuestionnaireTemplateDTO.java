package com.pantavanij.sourcingreq.services.domain.dto.eform;

import com.pantavanij.sourcingreq.services.domain.dto.TenantDto;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class QuestionnaireTemplateDTO {
    private Long recId;
    private TenantDto tenant;
    private String templateNo;
    private String templateName;
    private String templateDesc;
    private String serviceName;
    private String status;
    private String creatorName;
    private String modifierName;
    private String creator;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private Long serviceId;
    private Long statusId;
    private String versionNumber;
}
