package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.ERFXAdditionalDataItemDto;
import lombok.Data;

import java.util.List;

@Data
public class ERFXAdditionalDataRequest {
    private Integer erfxId;
    private String projectCode;
    private String projectName;
    private String projectLabel;
    private String objective;
    private Integer outsourceService;
    private String outsourceCode;
    private Integer requireSuppPerfEva;
    private String evaluationDate;
    private Integer makingContract;
    private String makingContractReason;
    private Integer makingRptContract;
    private String makingRptContractReason;
    private Integer vatAbsorbedBy;
    private Integer stampDutyAbsorbedBy;
    private Integer whtAbsorbedBy;
    private Integer needWhtCert;
    private List<ERFXAdditionalDataItemDto> items;
//    private ERFXRequesterInformationRequest requesterInformation;
    private List<ERFXQuestionnaireQuestionRequest> questionnaire;

    private String organization;
    private String costCenter;
    private String background;
    private String budgetRefNo;
    private String requestedDate;

    // PDPA Fields
    private Integer relatePdpa;
    private String thirdPartyRole;
    private String dpaType;
}