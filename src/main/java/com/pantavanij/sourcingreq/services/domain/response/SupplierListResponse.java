package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.SupplierDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierListResponse {
    private ApiResponseStatus status;

    private List<SupplierDto> supplierList;

    public SupplierListResponse(List<SupplierDto> supplierList) {
        this.supplierList = supplierList;
        this.status = new ApiResponseStatus();
    }
}
