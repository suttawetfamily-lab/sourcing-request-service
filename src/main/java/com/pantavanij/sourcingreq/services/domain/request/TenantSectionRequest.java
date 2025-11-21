package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;

import javax.validation.constraints.*;
import java.util.*;

@Data
public class TenantSectionRequest {
    @NotNull(message = "RecId is required!")
    @PositiveOrZero(message = "RecId must be positive or zero!")
    private Integer recId;
    @NotBlank(message = "Type is required!")
    private String type;
    private String sectionName;
    private String sectionTitle;
    @NotNull(message = "Step is required!")
    @PositiveOrZero(message = "Step must be positive or zero!")
    private Integer step;
    @NotNull(message = "Sequence is required!")
    @PositiveOrZero(message = "Sequence must be positive or zero!")
    private Integer sequence;
    @NotNull(message = "Visible is required!")
    private boolean visible;
    private Integer organizationId;
}
