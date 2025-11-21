package com.pantavanij.sourcingreq.services.domain.dto.supplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OracleAddressDto {
    @JsonProperty("AddressName")
    private String addressName;

    @JsonProperty("CountryCode")
    private String countryCode;

    @JsonProperty("County")
    private String county;

    @JsonProperty("AddressLine1")
    private String addressLine1;

    @JsonProperty("AddressLine2")
    private String addressLine2;

    @JsonProperty("AddressLine3")
    private String addressLine3;

    @JsonProperty("AddressLine4")
    private String addressLine4;

    @JsonProperty("Province")
    private String province;

    @JsonProperty("PostalCode")
    private String postalCode;

    @JsonProperty("AddressPurposeOrderingFlag")
    private Boolean addressPurposeOrderingFlag;

    @JsonProperty("AddressPurposeRemitToFlag")
    private Boolean addressPurposeRemitToFlag;

    @JsonProperty("AddressPurposeRFQOrBiddingFlag")
    private Boolean addressPurposeRFQOrBiddingFlag;
}
