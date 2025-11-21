package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.supplier.SupplierContactDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierContactResponse {
    private SupplierContactDto content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalPages;
}
