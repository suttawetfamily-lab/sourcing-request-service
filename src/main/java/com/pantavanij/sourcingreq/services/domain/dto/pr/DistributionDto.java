package com.pantavanij.sourcingreq.services.domain.dto.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DistributionDto {

    @JsonProperty("DistributionNumber")
    private Integer distributionNumber;

    @JsonProperty("CurrencyAmount")
    private BigDecimal currencyAmount;

    @JsonProperty("Quantity")
    private BigDecimal quantity;
}
