package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class SourcingGridFieldRequest {
    @NotNull(message = "SourcingGridField Id (RecId) is required!")
    private Integer recId;
    @NotBlank(message = "Code is required!")
    private String code;
    @NotBlank(message = "Type is required!")
    private String type;
    @NotBlank(message = "DisplayName is required!")
    private String displayName;
    @NotBlank(message = "PrivilegeCode is required!")
    private String privilegeCode;
    private Integer width;
    @PositiveOrZero(message = "Sequence must be a positive number or zero!")
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    @NotNull(message = "Visible is required!")
    private boolean visible;
    private String sorting;
    @NotNull(message = "Searchable is required!")
    private boolean searchable;
}
