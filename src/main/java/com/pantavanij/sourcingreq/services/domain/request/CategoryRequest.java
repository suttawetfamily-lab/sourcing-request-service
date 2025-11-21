package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.validation.constraints.*;
import java.util.*;

@Data
public class CategoryRequest {
    @NotNull(message = "Category recId is required!")
    private Integer recId;
    private List<Integer> purchasersId;
    @NotEmpty(message = "Project code is required!")
    private String code;
    @NotEmpty(message = "Project name is required!")
    private String name;
    @PositiveOrZero(message = "Category sequence must be a positive number or zero!")
    @NotNull(message = "Category sequence is required!")
    private Integer sequence;
    @JsonProperty("default")
    private boolean isDefault;
    private boolean active;
    List<SubCategoryRequest> subCategoryRequestList;
}
