package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class RequestItemGridFieldRequest {
    @NotNull(message = "RequestItemGridField Id (RecId) is required!")
    private Integer recId;
    @NotBlank(message = "Code is required!")
    private String code;
    @NotBlank(message = "Type is required!")
    private String type;
    @NotBlank(message = "DisplayName is required!")
    private String displayName;
    @NotBlank(message = "PrivilegeCode is required!")
    private String privilegeCode;
    private String align;
    private Integer width;
    @PositiveOrZero(message = "Sequence must be a positive number or zero!")
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    @NotNull(message = "Visible is required!")
    private boolean visible;
    private String tooltip;
    private String childField;
    private Integer organizationId;
}
