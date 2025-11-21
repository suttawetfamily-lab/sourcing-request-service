package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import lombok.Data;

@Data
public class CategoryPurchaserDto {
    private CategoryDto category;
    private PurchaserDto purchaser;
}
