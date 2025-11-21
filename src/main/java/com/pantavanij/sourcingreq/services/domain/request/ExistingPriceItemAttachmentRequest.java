package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ExistingPriceItemAttachmentRequest {
    @NotNull(message = "existingPriceItemId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "existingPriceItemId exceed limit {value} value")
    @Min(value = 0 ,message = "existingPriceItemId exceed limit {value} value")
    private Long existingPriceItemId;

    @NotNull(message = "attachmentId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "attachmentId exceed limit {value} value")
    @Min(value = 0 ,message = "attachmentId exceed limit {value} value")
    private Long attachmentId;

    @NotNull(message = "lineNum must not be null or empty" )
    @Max(value = 2147483647 ,message = "lineNum exceed limit {value} value")
    @Min(value = 0 ,message = "lineNum exceed limit {value} value")
    private Integer lineNum;

    @NotNull(message = "sendToSupplier must not be null or empty" )
    @Pattern(regexp = "^(0|1|true|false)$", message = "sendToSupplier field allowed input: true or false")
    private String sendToSupplier;

    @Size(max = 200 ,message = "note exceed limit {max} chars")
    private String note;
}
