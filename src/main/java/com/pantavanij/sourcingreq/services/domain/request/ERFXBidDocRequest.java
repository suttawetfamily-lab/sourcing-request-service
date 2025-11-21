package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ERFXBidDocRequest {
//    //Bid Type
//    String bidType;
//
//    //Header
//    String bidNo;
//    String sourcingRequestNo;
//    Timestamp date;
//    String krungsriEntity;
//    String department;
//    String telephoneNo;
//    String mobileNo;
//    String buyerName;
//    String projectName;
//    String projectCode;
//    String costCenter;
//    String productServiceDescription;
//    String objectives;
//    String background;
//
//    //Bidders
//    String bidderName;
//    String status;
//
//    //Price Comparison
//    String itemName;
//    String quantity;
//    String unit;
//    String vendor;
//    BigDecimal firstPrice;
//    BigDecimal unitPrice;
//    BigDecimal totalPrice;
//    BigDecimal latestPrice;
//    BigDecimal unitPrice2;// Dup ??
//    BigDecimal totalPrice2;// Dup ??
//    String awardVendor;
//    BigDecimal savingAmount;
//
//    //Price Condition
//    BigDecimal totalProjectPrice;
//    BigDecimal totalFinalPrice;
//    BigDecimal totalSavingAmount;
//    BigDecimal evaluationSummary;
//
//    //Template
//    String paymentTerm;
//    String warrantyMonthYear;
//    String category;
//    String subCategory;
//    Integer outsourceService;
//    Integer requireSupplierPerformanceEvaluation;
//    Integer makingContract;
//    String makingContractReason;
//    Integer makingRptContract;
//    String makingRptContractReason;
//    Integer vatAbsorbedBy;
//    Integer stampDutyAbsorbedBy;
//    Integer whtAbsorbedBy;
//    Integer needWhtCertificateForForeignOnly;
//
//    //Tax Advisory (Array ??)
//    String detailNo1Currency;
//    BigDecimal detailNo1AmountIncvat;
//    String detailNo1AvgDescription;
//    String detailNo1Remark;
//    String detailNo1PaymentType;
//    BigDecimal detailNo1AmountExclvat;
//    String detailNo1VatType;
//    String detailNo1WithholdingTax;
//    String detailNo2Currency;
//    BigDecimal detailNo2AmountIncvat;
//    String detailNo2AvgDescription;
//    String detailNo2remark;
//    String detailNo2PaymentType;
//    BigDecimal detailNo2Amountexclvat;
//    String detailNo2VatType;
//    String detailNo2WithholdingTax;
//    String actReferenceNo;
//    Timestamp _date;// ??
//    String relatedTaxImplication;
//
//    //Background
//    BigDecimal corporateIncomeTax;
//    BigDecimal withholdingTax;
//    BigDecimal valueAddedTax;
//    BigDecimal specificBusinessTax;
//    String stampDuty;
//    String other;
//
//    //Bid Evaluation Team Approve By
//    String approveNo;
//    String name;
//    String approveDepartment;
//    String approveStatus;
//    Timestamp approvedDate;
//    Timestamp evaluationDate;
//    String budgetRefNo;
//    String comment;
//    String footer1;// ??
//    String footer2;// ??
//    Timestamp requestedDate;

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