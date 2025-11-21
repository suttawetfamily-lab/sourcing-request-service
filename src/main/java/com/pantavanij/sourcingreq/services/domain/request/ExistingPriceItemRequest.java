package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ExistingPriceItemRequest {
    @NotNull(message = "recId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "recId exceed limit {value} value")
    @Min(value = 0 ,message = "recId exceed limit {value} value")
    private Long recId;

    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private Long requestId;

    @NotNull(message = "requestItemId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestItemId exceed limit {value} value")
    @Min(value = 0 ,message = "RequestItemId exceed limit {value} value")
    private Long requestItemId;

    @NotNull(message = "tenantId must not be null or empty" )
    @Max(value = 2147483647 ,message = "tenantId exceed limit {value} value")
    @Min(value = 0 ,message = "tenantId exceed limit {value} value")
    private Integer tenantId;

    @Size(max = 10 ,message = "materialCode exceed limit {max} chars")
    private String materialCode;

    @NotNull(message = "itemName must not be null or empty" )
    @NotBlank(message = "itemName must not be null or empty")
    @Size(max = 40 ,message = "itemName exceed limit {max} chars")
    private String itemName;

    @NotNull(message = "itemDescription must not be null or empty" )
    @NotBlank(message = "itemDescription must not be null or empty")
    @Size(max = 1000 ,message = "itemDescription exceed limit {max} chars")
    private String itemDescription;

    @Size(max = 255 ,message = "brand exceed limit {max} chars")
    private String brand;

    @Size(max = 255 ,message = "partNo exceed limit {max} chars")
    private String partNo;

    @NotNull(message = "unitObj must not be null or empty" )
    private OptionDto unitObj;

    @Digits(integer = 17 ,fraction = 2,message = "itemBudget exceed limit {integer} integer and {fraction} fraction")
    private BigDecimal itemBudget;

    @NotNull(message = "unitPrice must not be null or empty" )
    @Digits(integer = 17 ,fraction = 2,message = "unitPrice exceed limit {integer} integer and {fraction} fraction")
    private BigDecimal unitPrice;

    @Size(max = 500 ,message = "conditions exceed limit {max} chars")
    private String conditions;

    @Digits(integer = 15 ,fraction = 4,message = "quantity exceed limit {integer} integer and {fraction} fraction")
    private BigDecimal quantity;

    private String awardedQuantity;
    private String awardedAmount;
    
    @NotNull(message = "currencyObj must not be null")
    private OptionDto currencyObj;

    @Size(max = 2000 ,message = "comment exceed limit {max} chars")
    private String comment;

    private OptionDto supplierObj;

    private OptionDto vatTypeObj;

    private List<ExistingPriceItemAttachmentDto> existingPriceItemAttachmentList;

    @NotNull(message = "sourcingTypeId must not be null")
    private Integer sourcingTypeId;

    private String sourcingDocNo;

    @NotNull(message = "organizationId must not be null")
    private Integer organizationId;
}
