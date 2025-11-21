package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestItemDto {
    private Long recId;
    private String sourcingDocNo;
    private String sourcingDocId;
    private Integer sourcingTypeId;
    private SourcingStatusDto sourcingStatus;
    private Integer unitId;
    private String unitCode;
    private String purposeDescription;
    private String itemName;
    private String itemDescription;
    private BigDecimal itemBudget;
    private String brand;
    private String partNo;
    private String conditions;
    private BigDecimal quantity;
    private String deliveryLocation;
    private String contactName;
    private String phone;
    private String deletionReason;
    private String cancellationReason;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private ExistingPriceItemResponseDto existingPriceItemDto;
    private List<RequestItemLocationDto> requestItemLocationList;
    private List<RequestItemAttachmentDto> requestItemAttachmentList;
    private Timestamp bidValidityStartDate;
    private Timestamp bidValidityEndDate;
}
