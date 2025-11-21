package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.supplier.OracleAddressDto;
import com.pantavanij.sourcingreq.services.domain.dto.supplier.OracleContactDto;
import com.pantavanij.sourcingreq.services.domain.dto.supplier.OracleSiteDto;
import lombok.Data;

import java.util.List;

@Data
public class CreateSupplierResponse {

    @JsonProperty("SupplierId")
    private Long supplierId;

    @JsonProperty("Supplier")
    private String supplier;

    @JsonProperty("SupplierNumber")
    private String supplierNumber;

    @JsonProperty("AlternateName")
    private String alternateName;

    @JsonProperty("TaxOrganizationType")
    private String taxOrganizationType;

    @JsonProperty("SupplierType")
    private String supplierType;

    @JsonProperty("BusinessRelationship")
    private String businessRelationship;

    @JsonProperty("OneTimeSupplierFlag")
    private Boolean oneTimeSupplierFlag;

    @JsonProperty("TaxRegistrationCountryCode")
    private String taxRegistrationCountryCode;

    @JsonProperty("TaxRegistrationNumber")
    private String taxRegistrationNumber;

    @JsonProperty("addresses")
    private List<OracleAddressDto> addresses;

    @JsonProperty("contacts")
    private List<OracleContactDto> contacts;

    @JsonProperty("sites")
    private List<OracleSiteDto> sites;
}
