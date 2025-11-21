package com.pantavanij.sourcingreq.services.domain.response.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "core_contact")
public class ContactDocument {

    @Id
    @JsonProperty("_id")
    private Integer id;

    @JsonProperty("branchNo")
    private String branchNo;

    @JsonProperty("buyerMPId")
    private String buyerMPId;

    @JsonProperty("businessNameLocal")
    private String businessNameLocal;

    @JsonProperty("businessNameInter")
    private String businessNameInter;

//    @JsonProperty("categories")
//    private String categories;

    @JsonProperty("contactNamelocal")
    private String contactNameLocal;

    @JsonProperty("contactNameInter")
    private String contactNameInter;

    @JsonProperty("email")
    private String email;

    @JsonProperty("erfxOrgId")
    private String erfxOrgId;

//    @JsonProperty("eId")
//    private String eId;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @JsonProperty("isDisabled")
    private Boolean isDisabled;

    @JsonProperty("isPublic")
    private Boolean isPublic;

    @JsonProperty("isPtvnVerified")
    private Boolean isPtvnVerified;

    @JsonProperty("mergeToUserId")
    private String mergeToUserId;

    @JsonProperty("orgId")
    private String orgId;

    @JsonProperty("orgNameLocal")
    private String orgNameLocal;

    @JsonProperty("orgNameInter")
    private String orgNameInter;

    @JsonProperty("portalUserId")
    private String portalUserId;

    @JsonProperty("province")
    private String province;

    @JsonProperty("supplierId")
    private Integer supplierId;

    @JsonProperty("taxId")
    private String taxId;

//    @JsonProperty("vendorNo")
//    private String vendorNo;
}

