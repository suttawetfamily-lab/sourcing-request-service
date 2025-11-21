package com.pantavanij.sourcingreq.services.domain.response.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrDistributionResponse {

    @JsonProperty("RequisitionDistributionId")
    private Long requisitionDistributionId;

    @JsonProperty("RequisitionLineId")
    private Long requisitionLineId;

    @JsonProperty("Quantity")
    private BigDecimal quantity;

    @JsonProperty("DistributionNumber")
    private Integer distributionNumber;

    @JsonProperty("CurrencyAmount")
    private BigDecimal currencyAmount;

    @JsonProperty("ChartOfAccountId")
    private Long chartOfAccountId;

    @JsonProperty("UserAccountOverrideFlag")
    private Boolean userAccountOverrideFlag;

    @JsonProperty("FundsStatusCode")
    private String fundsStatusCode;
}