package com.pantavanij.sourcingreq.services.domain.response.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PrLineResponse {
    @JsonProperty("RequisitionHeaderId")
    private Long requisitionHeaderId;

    @JsonProperty("RequisitionLineId")
    private Long requisitionLineId;

    @JsonProperty("LineNumber")
    private Integer lineNumber;

    @JsonProperty("LineTypeId")
    private Long lineTypeId;

    @JsonProperty("LineTypeCode")
    private String lineTypeCode;

    @JsonProperty("LineType")
    private String lineType;

    @JsonProperty("RequisitionLineSource")
    private String requisitionLineSource;

    @JsonProperty("CategoryId")
    private Long categoryId;

    @JsonProperty("CategoryName")
    private String categoryName;

    @JsonProperty("ItemDescription")
    private String itemDescription;

    @JsonProperty("Quantity")
    private BigDecimal quantity;

    @JsonProperty("UnitPrice")
    private BigDecimal unitPrice;

    @JsonProperty("CurrencyCode")
    private String currencyCode;

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("UOMCode")
    private String uomCode;

    @JsonProperty("UOM")
    private String uom;

    @JsonProperty("Price")
    private BigDecimal price;

    @JsonProperty("LineStatus")
    private String lineStatus;

    @JsonProperty("NegotiatedByPreparerFlag")
    private Boolean negotiatedByPreparerFlag;

    @JsonProperty("SupplierId")
    private Long supplierId;

    @JsonProperty("Supplier")
    private String supplier;

    @JsonProperty("SupplierSiteId")
    private Long supplierSiteId;

    @JsonProperty("SupplierSite")
    private String supplierSite;

    @JsonProperty("RequesterId")
    private Long requesterId;

    @JsonProperty("Requester")
    private String requester;

    @JsonProperty("RequesterDisplayName")
    private String requesterDisplayName;

    @JsonProperty("RequesterEmail")
    private String requesterEmail;

    @JsonProperty("RequestedDeliveryDate")
    private String requestedDeliveryDate;

    @JsonProperty("DestinationTypeCode")
    private String destinationTypeCode;

    @JsonProperty("DestinationType")
    private String destinationType;

    @JsonProperty("DestinationOrganizationId")
    private Long destinationOrganizationId;

    @JsonProperty("DestinationOrganizationCode")
    private String destinationOrganizationCode;

    @JsonProperty("DestinationOrganization")
    private String destinationOrganization;

    @JsonProperty("OneTimeLocationFlag")
    private Boolean oneTimeLocationFlag;

    @JsonProperty("DeliverToLocationId")
    private Long deliverToLocationId;

    @JsonProperty("DeliverToLocationCode")
    private String deliverToLocationCode;

    @JsonProperty("DeliverToAddress")
    private String deliverToAddress;

    @JsonProperty("FormattedDeliverToAddress")
    private String formattedDeliverToAddress;

    @JsonProperty("ProcurementBUId")
    private Long procurementBUId;

    @JsonProperty("ProcurementBU")
    private String procurementBU;

    @JsonProperty("SourceTypeCode")
    private String sourceTypeCode;

    @JsonProperty("SourceType")
    private String sourceType;

    @JsonProperty("ConfiguredItemFlag")
    private Boolean configuredItemFlag;

    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("CreationDate")
    private String creationDate;

    @JsonProperty("LastUpdatedBy")
    private String lastUpdatedBy;

    @JsonProperty("LastUpdateDate")
    private String lastUpdateDate;

    @JsonProperty("RequisitionLineGroup")
    private String requisitionLineGroup;

    @JsonProperty("ProductType")
    private String productType;

    @JsonProperty("ProductTypeCode")
    private String productTypeCode;

    @JsonProperty("DisableAutosourceFlag")
    private Boolean disableAutosourceFlag;

    @JsonProperty("LineTypeOrderTypeLookupCode")
    private String lineTypeOrderTypeLookupCode;

    @JsonProperty("FundsStatusCode")
    private String fundsStatusCode;

    @JsonProperty("TaxationCountryCode")
    private String taxationCountryCode;

    @JsonProperty("DestinationOrganizationLegalEntityId")
    private Long destinationOrganizationLegalEntityId;

    @JsonProperty("TaxationCountry")
    private String taxationCountry;

    @JsonProperty("distributions")
    private List<PrDistributionResponse> distributions;
}
