package com.pantavanij.sourcingreq.services.domain.dto.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LineDto {

    @JsonProperty("LineNumber")
    private Integer lineNumber;

    @JsonProperty("DestinationTypeCode")
    private String destinationTypeCode;

    @JsonProperty("DeliverToLocationCode")
    private String deliverToLocationCode;

    @JsonProperty("DestinationOrganizationCode")
    private String destinationOrganizationCode;

    @JsonProperty("RequesterEmail")
    private String requesterEmail;

    @JsonProperty("Quantity")
    private BigDecimal quantity;

    @JsonProperty("UOM")
    private String uom;

    @JsonProperty("ItemDescription")
    private String itemDescription;

    @JsonProperty("CategoryName")
    private String categoryName;

    @JsonProperty("LineTypeCode")
    private String lineTypeCode;

    @JsonProperty("CurrencyCode")
    private String currencyCode;

    @JsonProperty("Price")
    private BigDecimal price;

    @JsonProperty("Supplier")
    private String supplier;

    @JsonProperty("SupplierSite")
    private String supplierSite;

    @JsonProperty("NegotiatedByPreparerFlag")
    private boolean negotiatedByPreparerFlag;

    @JsonProperty("RequestedDeliveryDate")
    private String requestedDeliveryDate;

    @JsonProperty("attachments")
    private List<AttachmentDto> attachments;

    @JsonProperty("DFF")
    private List<DFFTicketTypeDto> dff;

    @JsonProperty("distributions")
    private List<DistributionDto> distributions;
}
