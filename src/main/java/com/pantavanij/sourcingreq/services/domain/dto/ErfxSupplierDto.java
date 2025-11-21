package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import javax.validation.constraints.*;
import java.math.*;

@Data
public class ErfxSupplierDto {
    private BigDecimal awardedValue;
    private String basePrice;
    @NotNull(message = "Unit price is required.")
    private BigDecimal unitPrice;
    private String awardedVendorName;
    private String supplier;
    private String shortName;
    private String taxNo;
}
