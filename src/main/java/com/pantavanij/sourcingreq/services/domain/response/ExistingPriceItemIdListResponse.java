package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemDtoV2;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class ExistingPriceItemIdListResponse {
    private List<Long> data;
    private ApiResponseStatus status;

    public ExistingPriceItemIdListResponse(List<Long> data) {
        this.data = data;
        this.status = new ApiResponseStatus();
    }

    public ExistingPriceItemIdListResponse(List<Long> data, ApiResponseStatus status) {
        this.data = data;
        this.status = status;
    }
}
