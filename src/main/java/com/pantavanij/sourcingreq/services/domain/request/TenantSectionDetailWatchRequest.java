package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;

import javax.validation.constraints.*;
import java.util.*;

@Data
public class TenantSectionDetailWatchRequest {
    @NotNull(message = "Tenant section id is required!")
    @PositiveOrZero(message = "Tenant section id must be positive or zero!")
    private Integer tenantSectionDetailId;
    @NotNull(message = "Tenant section detail dependency sequence is required!")
    @PositiveOrZero(message = "Tenant section detail dependency sequence must be positive or zero!")
    private Integer sequence;
    private String groupName;
    private String fieldName;
    private String originalFieldName;
    private String updatedFieldName;
    private List<String> values;
}
