package com.pantavanij.sourcingreq.services.domain.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierContactDto {
    SupplierCategoryDto supplierCategory;
    List<SupplierInfoDto> suppliers;
}
