package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.UnitDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UploadRequestItemRequest {
    private Long recId;
    private Long requestId;
    private Integer tenantId;
    private String purposeDescription;
    private String itemName;
    private String itemDescription;
    private String conditions;
    private BigDecimal quantity;
    private UnitDto unit;
    private Integer locationId;
    private String locationName;
    private String deliveryLocation;
    private String contactName;
    private String phone;
}
