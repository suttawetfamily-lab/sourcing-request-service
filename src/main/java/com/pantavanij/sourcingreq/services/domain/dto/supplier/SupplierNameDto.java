package com.pantavanij.sourcingreq.services.domain.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierNameDto {
    private String invNameLocal;
    private String invNameInter;
}
