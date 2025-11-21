package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExistingPriceItemResponse {
    private ApiResponseStatus status;

    private Long existingPriceItemId;
    private String sourcingDocNo;

    public ExistingPriceItemResponse(Long existingPriceItemId) {
        this.existingPriceItemId = existingPriceItemId;
        this.status = new ApiResponseStatus();
    }

    public ExistingPriceItemResponse(Long existingPriceItemId, String sourcingDocNo) {
        this.existingPriceItemId = existingPriceItemId;
        this.sourcingDocNo = sourcingDocNo;
        this.status = new ApiResponseStatus();
    }
}
