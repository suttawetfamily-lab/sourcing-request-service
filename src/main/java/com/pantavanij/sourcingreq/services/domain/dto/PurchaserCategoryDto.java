package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.*;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaserCategoryDto {
    private CategoryDto category;
}
