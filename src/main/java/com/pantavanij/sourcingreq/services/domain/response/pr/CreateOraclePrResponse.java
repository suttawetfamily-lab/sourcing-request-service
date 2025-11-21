package com.pantavanij.sourcingreq.services.domain.response.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateOraclePrResponse {

    @JsonProperty("Message")
    private String message;

    @JsonProperty("RequisitionHeaderId")
    private Long requisitionHeaderId;

    @JsonProperty("Requisition")
    private String requisition;

    @JsonProperty("RequisitioningBUId")
    private Long requisitioningBUId;

    @JsonProperty("RequisitioningBU")
    private String requisitioningBU;

    @JsonProperty("PreparerId")
    private Long preparerId;

    @JsonProperty("Preparer")
    private String preparer;

    @JsonProperty("PreparerEmail")
    private String preparerEmail;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Justification")
    private String justification;

    @JsonProperty("DocumentStatus")
    private String documentStatus;

    @JsonProperty("DocumentStatusCode")
    private String documentStatusCode;

    @JsonProperty("BudgetaryControlEnabledFlag")
    private Boolean budgetaryControlEnabledFlag;

    @JsonProperty("EmergencyRequisitionFlag")
    private Boolean emergencyRequisitionFlag;

    @JsonProperty("ExternallyManagedFlag")
    private Boolean externallyManagedFlag;

    @JsonProperty("InternalTransferFlag")
    private Boolean internalTransferFlag;

    @JsonProperty("FundsChkFailWarnFlag")
    private Boolean fundsChkFailWarnFlag;

    @JsonProperty("CreationDate")
    private String creationDate;

    @JsonProperty("LastUpdateDate")
    private String lastUpdateDate;

    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("LastUpdatedBy")
    private String lastUpdatedBy;

    @JsonProperty("ActiveRequisitionFlag")
    private Boolean activeRequisitionFlag;

    @JsonProperty("RequisitionLineGroup")
    private String requisitionLineGroup;

    @JsonProperty("InsufficientFundsFlag")
    private Boolean insufficientFundsFlag;

    @JsonProperty("SpecialHandlingTypeCode")
    private String specialHandlingTypeCode;

    @JsonProperty("SpecialHandlingType")
    private String specialHandlingType;

    @JsonProperty("TaxationCountryCode")
    private String taxationCountryCode;

    @JsonProperty("TaxationCountry")
    private String taxationCountry;

    @JsonProperty("TaxAttrsUserOverrideHeaderFlag")
    private Boolean taxAttrsUserOverrideHeaderFlag;

    @JsonProperty("lines")
    private List<PrLineResponse> lines;

    @JsonProperty("DFF")
    private List<PrDffResponse> dff;

    @JsonProperty("attachments")
    private List<PrAttachmentResponse> attachments;
}
