package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantSectionDetailDescriptionDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer recId;
    private String description;
    private String descriptionLabelAction;
    private String descriptionAction;
    private Integer disable;
}
