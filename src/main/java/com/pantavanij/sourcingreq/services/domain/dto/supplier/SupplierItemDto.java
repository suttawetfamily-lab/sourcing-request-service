package com.pantavanij.sourcingreq.services.domain.dto.supplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SupplierItemDto {

    @JsonProperty("SupplierId")
    private Long supplierId;

    @JsonProperty("SupplierPartyId")
    private Long supplierPartyId;

    @JsonProperty("Supplier")
    private String supplier;

    @JsonProperty("SupplierNumber")
    private String supplierNumber;

    @JsonProperty("AlternateName")
    private String alternateName;

    @JsonProperty("TaxOrganizationTypeCode")
    private String taxOrganizationTypeCode;

    @JsonProperty("TaxOrganizationType")
    private String taxOrganizationType;

    @JsonProperty("SupplierTypeCode")
    private String supplierTypeCode;

    @JsonProperty("SupplierType")
    private String supplierType;

    @JsonProperty("InactiveDate")
    private String inactiveDate;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("BusinessRelationshipCode")
    private String businessRelationshipCode;

    @JsonProperty("BusinessRelationship")
    private String businessRelationship;

    @JsonProperty("ParentSupplierId")
    private Long parentSupplierId;

    @JsonProperty("ParentSupplier")
    private String parentSupplier;

    @JsonProperty("ParentSupplierNumber")
    private String parentSupplierNumber;

    @JsonProperty("CreationDate")
    private String creationDate;

    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("LastUpdateDate")
    private String lastUpdateDate;

    @JsonProperty("LastUpdatedBy")
    private String lastUpdatedBy;

    @JsonProperty("CreationSourceCode")
    private String creationSourceCode;

    @JsonProperty("CreationSource")
    private String creationSource;

    @JsonProperty("DataFoxScore")
    private String dataFoxScore;

    @JsonProperty("DataFoxScoringCriteria")
    private String dataFoxScoringCriteria;

    @JsonProperty("Alias")
    private String alias;

    @JsonProperty("DUNSNumber")
    private String dunsNumber;

    @JsonProperty("OneTimeSupplierFlag")
    private Boolean oneTimeSupplierFlag;

    @JsonProperty("RegistryId")
    private String registryId;

    @JsonProperty("CustomerNumber")
    private String customerNumber;

    @JsonProperty("StandardIndustryClass")
    private String standardIndustryClass;

    @JsonProperty("IndustryCategory")
    private String industryCategory;

    @JsonProperty("IndustrySubcategory")
    private String industrySubcategory;

    @JsonProperty("NationalInsuranceNumber")
    private String nationalInsuranceNumber;

    @JsonProperty("NationalInsuranceNumberExistsFlag")
    private Boolean nationalInsuranceNumberExistsFlag;

    @JsonProperty("CorporateWebsite")
    private String corporateWebsite;

    @JsonProperty("YearEstablished")
    private String yearEstablished;

    @JsonProperty("MissionStatement")
    private String missionStatement;

    @JsonProperty("YearIncorporated")
    private String yearIncorporated;

    @JsonProperty("ChiefExecutiveTitle")
    private String chiefExecutiveTitle;

    @JsonProperty("ChiefExecutiveName")
    private String chiefExecutiveName;

    @JsonProperty("PrincipalTitle")
    private String principalTitle;

    @JsonProperty("PrincipalName")
    private String principalName;

    @JsonProperty("FiscalYearEndMonthCode")
    private String fiscalYearEndMonthCode;

    @JsonProperty("FiscalYearEndMonth")
    private String fiscalYearEndMonth;

    @JsonProperty("CurrentFiscalYearPotentialRevenue")
    private String currentFiscalYearPotentialRevenue;

    @JsonProperty("PreferredFunctionalCurrencyCode")
    private String preferredFunctionalCurrencyCode;

    @JsonProperty("PreferredFunctionalCurrency")
    private String preferredFunctionalCurrency;

    @JsonProperty("TaxRegistrationCountryCode")
    private String taxRegistrationCountryCode;

    @JsonProperty("TaxRegistrationCountry")
    private String taxRegistrationCountry;

    @JsonProperty("TaxRegistrationNumber")
    private String taxRegistrationNumber;

    @JsonProperty("TaxpayerCountryCode")
    private String taxpayerCountryCode;

    @JsonProperty("TaxpayerCountry")
    private String taxpayerCountry;

    @JsonProperty("TaxpayerId")
    private String taxpayerId;

    @JsonProperty("TaxpayerIdExistsFlag")
    private Boolean taxpayerIdExistsFlag;

    @JsonProperty("FederalReportableFlag")
    private Boolean federalReportableFlag;

    @JsonProperty("FederalIncomeTaxTypeCode")
    private String federalIncomeTaxTypeCode;

    @JsonProperty("FederalIncomeTaxType")
    private String federalIncomeTaxType;

    @JsonProperty("StateReportableFlag")
    private Boolean stateReportableFlag;

    @JsonProperty("TaxReportingName")
    private String taxReportingName;

    @JsonProperty("NameControl")
    private String nameControl;

    @JsonProperty("VerificationDate")
    private String verificationDate;

    @JsonProperty("UseWithholdingTaxFlag")
    private Boolean useWithholdingTaxFlag;

    @JsonProperty("WithholdingTaxGroupId")
    private String withholdingTaxGroupId;

    @JsonProperty("WithholdingTaxGroup")
    private String withholdingTaxGroup;

    @JsonProperty("BusinessClassificationNotApplicableFlag")
    private Boolean businessClassificationNotApplicableFlag;

    @JsonProperty("DataFoxId")
    private String dataFoxId;

    @JsonProperty("DataFoxCompanyName")
    private String dataFoxCompanyName;

    @JsonProperty("DataFoxLegalName")
    private String dataFoxLegalName;

    @JsonProperty("DataFoxCompanyPrimaryURL")
    private String dataFoxCompanyPrimaryURL;

    @JsonProperty("DataFoxNAICSCode")
    private String dataFoxNAICSCode;

    @JsonProperty("DataFoxCountry")
    private String dataFoxCountry;

    @JsonProperty("DataFoxEIN")
    private String dataFoxEIN;

    @JsonProperty("DataFoxLastSyncDate")
    private String dataFoxLastSyncDate;

    @JsonProperty("OBNEnabledFlag")
    private Boolean obnEnabledFlag;

    @JsonProperty("OnOFACListFlag")
    private Boolean onOFACListFlag;

    @JsonProperty("OFACSources")
    private String ofacSources;
}

