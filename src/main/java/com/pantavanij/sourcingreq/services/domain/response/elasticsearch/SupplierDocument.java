package com.pantavanij.sourcingreq.services.domain.response.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.CoreCountryDto;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.CoreOrgAddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "core_supplier")
public class SupplierDocument {

    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("invitationCode")
    private String invitationCode;

    @JsonProperty("orgId")
    private String orgId;

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("companyNameLocal")
    private String companyNameLocal;

    @JsonProperty("companyNameInter")
    private String companyNameInter;

    @JsonProperty("invNameLocal")
    private String invNameLocal;

    @JsonProperty("invNameInter")
    private String invNameInter;

    @JsonProperty("currencyCode")
    private String currencyCode;

    @JsonProperty("mainBusinessCode")
    private String mainBusinessCode;

    @JsonProperty("phoneNo")
    private String phoneNo;

    @JsonProperty("phoneExt")
    private String phoneExt;

    @JsonProperty("mobileNo")
    private String mobileNo;

    @JsonProperty("faxNo")
    private String faxNo;

    @JsonProperty("faxExt")
    private String faxExt;

    @JsonProperty("webSite")
    private String webSite;

    @JsonProperty("registeredCapital")
    private Long registeredCapital;

    @JsonProperty("yearEstablished")
    private String yearEstablished;

    @JsonProperty("latitude")
    private Integer latitude;

    @JsonProperty("longtitude")
    private Integer longtitude;

    @JsonProperty("taxId")
    private String taxId;

    @JsonProperty("isDisplay")
    private String isDisplay;

    @JsonProperty("isContactBlock")
    private Boolean isContactBlock;

    @JsonProperty("coreCountry")
    private CoreCountryDto coreCountry;

    @JsonProperty("coreOrgAddressCompany")
    private CoreOrgAddressDto coreOrgAddressCompany;

}