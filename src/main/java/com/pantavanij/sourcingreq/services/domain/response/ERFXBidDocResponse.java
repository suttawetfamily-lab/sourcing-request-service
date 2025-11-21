package com.pantavanij.sourcingreq.services.domain.response;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ERFXBidDocResponse {
    private String docNum;
    private String organization;
    private String department;
    private String phone;
    private String mobile;
    private String projectCode;
    private String projectName;
    private String projectLabel;
    private String costcenter;
    private String objective;
    private String background;

    private String category;
    private String subCategory;

    private Integer outsourceService;
    private String outsourceCode;
    private Integer requireSuppPerfEva;
    private Integer makingContract;
    private String makingContractReason;
    private Integer makingRptContract;
    private String makingRptContractReason;
    private Integer vatAbsorbedBy;
    private Integer stampDutyAbsorbedBy;
    private Integer whtAbsorbedBy;
    private Integer needWhtCert;
    private Timestamp evaluationDate;
    private String budgetRefNo;
    private Timestamp requestedDate;
}