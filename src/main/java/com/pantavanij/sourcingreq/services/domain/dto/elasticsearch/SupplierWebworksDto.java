package com.pantavanij.sourcingreq.services.domain.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupplierWebworksDto {
    private String fullCompanyNameLocal;
    private String fullCompanyNameEN;
    private String companyNameLocal;
    private String companyNameEN;
    private String branchNameLocal;
    private String branchNameEN;
    private String branchNumber;
    @JsonProperty("TPShortName")
    private String TPShortName;
    private String taxId;


//    private String orgId;
    private String branch;
//    private String companyNameLocal;
//    private String companyNameInter;
//    private String invNameLocal;
//    private String invNameInter;
    private String currencyCode;
    private String mainBusinessCode;
    private String phoneNo;
    private String phoneExt;
    private String mobileNo;
    private String faxNo;
    private String faxExt;
    private String webSite;
    private Long registeredCapital;
    private String yearEstablished;
    private Integer latitude;
    private Integer longtitude;
//    private String taxId;
    private String isDisplay;
    private Boolean isContactBlock;
    private CoreCountryDto coreCountry;
    private CoreOrgAddressDto coreOrgAddressCompany;
    private List<CoreContactPersonDto> coreContactPersons;







}
