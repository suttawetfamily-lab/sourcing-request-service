package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ShortlistDto {
//    private String basePrice;
    private BigDecimal costAvoidanceVat7Percentage;
    private BigDecimal quantity;
    private BigDecimal totalProposePrice;

    private List<ErfxSupplierDto> suppliers;
    private List<ERFXAttachmentDto> erfxItemAttachments;
    @NotNull(message = "itemId is required")
    private Long itemId;
    @NotNull(message = "erfxItemId is required")
    private Long erfxItemId;
    private Long erfxItemOrder;

    @NotEmpty(message = "itemName is required")
    private String itemName;
    @NotEmpty(message = "itemDetail is required")
    private String itemDetail;
    private String condition;
    @NotEmpty(message = "unit is required")
    private String unit;
    @JsonProperty("MONTH")
    private String bidCompleteMonth;
    @JsonProperty("YEAR")
    private String bidCompleteYear;
    private String bidNo;
    private String biddingType;
    private String bidDescription;
    private String vatType;
    private String itemStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Timestamp bidValidityStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Timestamp bidValidityEndDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Timestamp bidStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Timestamp bidCompleteDate;

}
