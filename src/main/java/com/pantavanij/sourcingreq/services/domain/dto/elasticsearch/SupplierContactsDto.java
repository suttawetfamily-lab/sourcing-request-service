package com.pantavanij.sourcingreq.services.domain.dto.elasticsearch;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "core_contact")
public class SupplierContactsDto {

    private String branchNo;
    private String buyerMPId;
    private String businessNameLocal;
    private String businessNameInter;
//    private String categories;
    private String contactNameLocal;
    private String contactNameInter;
    private String email;
    private String erfxOrgId;
//    private String eId;
    private Boolean isDeleted;
    private Boolean isDisabled;
    private Boolean isPublic;
    private Boolean isPtvnVerified;
    private String mergeToUserId;
    private String orgId;
    private String orgNameLocal;
    private String orgNameInter;
    private String portalUserId;
    private String province;
    private Integer supplierId;
    private String taxId;
//    private String vendorNo;
}

