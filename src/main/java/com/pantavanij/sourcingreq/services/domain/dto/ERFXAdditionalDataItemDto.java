package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ERFXAdditionalDataItemDto {
    private Integer itemId;
    private String bidValidityStartDate;
    private String bidValidityEndDate;
    private String category;
    private String subCategory;
}