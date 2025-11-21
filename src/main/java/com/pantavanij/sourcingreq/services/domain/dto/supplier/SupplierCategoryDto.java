package com.pantavanij.sourcingreq.services.domain.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierCategoryDto {
    private String categoryValueLocalLev1;
    private String categoryValueInterLev1;
    private String categoryValueLocalLev2;
    private String categoryValueInterLev2;
    private String categoryValueLocalLev3;
    private String categoryValueInterLev3;

}
