package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.validation.constraints.*;
import java.util.*;

@Data
public class SubCategoryRequest {
    @NotNull(message = "Sub-category recId is required!")
    private Integer recId;
    @NotNull(message = "Category recId is required!")
    private Integer categoryId;
    private List<Integer> purchasersId;
    @NotEmpty(message = "Project code is required!")
    private String code;
    @NotEmpty(message = "Project name is required!")
    private String name;
    @PositiveOrZero(message = "Sub-category sequence must be a positive number or zero!")
    @NotNull(message = "Sub-category sequence is required!")
    private Integer sequence;
    @JsonProperty("default")
    private boolean isDefault;
    private boolean active;
    private boolean isReOrderSequence;
}
