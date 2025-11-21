package com.pantavanij.sourcingreq.services.domain.request.supplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.supplier.OracleAddressDto;
import com.pantavanij.sourcingreq.services.domain.dto.supplier.OracleContactDto;
import com.pantavanij.sourcingreq.services.domain.dto.supplier.OracleSiteDto;
import lombok.Data;

import java.util.List;

@Data
public class CreateSupplierRequest {

    @JsonProperty("Supplier")
    private String supplier;

    @JsonProperty("TaxOrganizationType")
    private String taxOrganizationType;

    @JsonProperty("SupplierType")
    private String supplierType;

    @JsonProperty("BusinessRelationship")
    private String businessRelationship;

    @JsonProperty("TaxRegistrationCountryCode")
    private String taxRegistrationCountryCode;

    @JsonProperty("OneTimeSupplierFlag")
    private Boolean oneTimeSupplierFlag;

    @JsonProperty("TaxRegistrationNumber")
    private String taxRegistrationNumber;

    @JsonProperty("UseWithholdingTaxFlag")
    private Boolean useWithholdingTaxFlag; // จาก JSON เป็น "false" (string)

    @JsonProperty("addresses")
    private List<OracleAddressDto> addresses;

    @JsonProperty("sites")
    private List<OracleSiteDto> sites;

    @JsonProperty("contacts")
    private List<OracleContactDto> contacts;
}
