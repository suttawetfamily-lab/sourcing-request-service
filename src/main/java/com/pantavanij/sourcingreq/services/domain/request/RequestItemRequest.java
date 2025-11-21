package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemAttachmentDto;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class RequestItemRequest {
    @NotNull(message = "recId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "recId exceed limit {value} value")
    @Min(value = 0 ,message = "recId exceed limit {value} value")
    private Long recId;

    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private Long requestId;

    @NotNull(message = "tenantId must not be null or empty" )
    @Max(value = 2147483647 ,message = "tenantId exceed limit {value} value")
    @Min(value = 0 ,message = "tenantId exceed limit {value} value")
    private Integer tenantId;

    @Size(max = 140 ,message = "purposeDescription exceed limit {max} chars")
    private String purposeDescription;

    @NotNull(message = "itemName must not be null or empty" )
    @NotBlank(message = "itemName must not be null or empty")
    @Size(max = 40 ,message = "itemName exceed limit {max} chars")
    private String itemName;

    @NotNull(message = "itemDescription must not be null or empty" )
    @NotBlank(message = "itemDescription must not be null or empty")
    @Size(max = 1000 ,message = "itemDescription exceed limit {max} chars")
    private String itemDescription;

    @Size(max = 500 ,message = "conditions exceed limit {max} chars")
    private String conditions;

    @NotNull(message = "quantity must not be null or empty" )
    @Digits(integer = 15 ,fraction = 4,message = "quantity exceed limit {integer} integer and {fraction} fraction")
    private BigDecimal quantity;

    @NotNull(message = "unitObj must not be null or empty" )
    private OptionDto unitObj;

    private LocationDto deliveryLocation;

    @Size(max = 500 ,message = "location exceed limit {max} chars")
    private String location;

    @Size(max = 100 ,message = "contactName exceed limit {max} chars")
    private String contactName;
    
    @Size(max = 50 ,message = "contactPhone exceed limit {max} chars")
    private String contactPhone;

    private OptionDto purposeObj;
    private OptionDto categoryObj;
    private OptionDto subCategoryObj;
    private OptionDto currencyObj;
    private String brand;
    private String partNo;

    @Digits(integer=17, fraction=2)
    private BigDecimal itemBudget;
    private String purchaser;
    private Timestamp bidValidityStartDate;
    private Timestamp bidValidityEndDate;
    private String no;
    private Integer itemSequence;

    private List<RequestItemAttachmentDto> requestItemAttachmentList;
}
