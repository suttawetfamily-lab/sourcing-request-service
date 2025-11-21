package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ERFXItemDto {
    private Integer itemId;
    private String materialCode;
    private String itemName;
    private String itemDetail;
    private BigDecimal quantity;
    private String condition;
    private String unit;
    private BigDecimal refUnitPrice;
}