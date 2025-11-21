package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ExcelRequestItemDto {
    private String requestNo;
    private Integer requestTypeId;
    private String requestName;
    private String projectCode;
    private String projectName;
    private String projectLabel;

    private String budgetRefNo; //new column
    private BigDecimal budget; //new column

    private Timestamp requestDate;
    private Timestamp expectedDate;
    private String createdBy;
    private String requestStatus;
    private Integer sourcingTypeId;
    private String sourcingTypeName;
    private String sourcingDocNo;
    private String purposeDescription;
    private String itemName;
    private String existingPriceItemId;
    private String existingPriceItemName;
    private String itemDescription;
    private String existingPriceItemDescription;
    private String existingPriceComment;
    private String brand;
    private String partNo;
    private BigDecimal quantity;
    private String conditions;
    private String unitCode;

    private BigDecimal itemBudget; //new column

    private BigDecimal unitPrice;
    private String currencyCode;


    private String supplierName;
    private String eXSupplierName;
    private String deliveryLocation;
    private String locationName;
    private String contactName;
    private String phone;


    private String assignedBy;
    private Integer sourcingStatusId;
    private String sourcingStatusName;
    private Integer locationId;
    private String eXUnitCode;
    private Integer requestItemId;
    private String typeCode;
    private String typeName;
    private String objective;
    private String objectiveCode;
    private String objectiveName;

    // BAY Customize
    private Integer organizationId;
    private Timestamp bidStartDate;
    private Timestamp bidCompleteDate;
    private String bidCompleteMonth;
    private String bidCompleteYear;
    private String buyerName;
    private String bidNo;
    private String biddingType;
    private String bidDescription;
    private String awardedVendorName;
    private String taxNo;
    private String categoryName;
    private String subCategoryName;
    private Timestamp bidValidityStartDate;
    private Timestamp bidValidityEndDate;
    private String departmentName;
    private String requestAdditionalDepartment;
    private String vatType;
    private BigDecimal totalProjectedPrice;
    private BigDecimal totalProjectedPriceVat7Percentage;
    private BigDecimal totalFinalPrice;
    private BigDecimal totalFinalPriceVat7Percentage;
    private BigDecimal totalSavingAmount;
    private BigDecimal totalSavingAmountVat7Percentage;
    private BigDecimal costAvoidanceVat7Percentage;
    private BigDecimal savingPercentage;

    private String DelegateActionBy;
    private Integer sourcingItemSequence;
    private Integer itemSequence;
    private Integer vatTypeId;

    private Boolean withdraw;
    private String withdrawReason;
    private String companyCode;
    private String companyName;

    private BigDecimal awardedQuantity;
    private String awardedAmount;
    private BigDecimal awardedNetAmount;
    private String awardedValue;
    private String awardedType;
}
