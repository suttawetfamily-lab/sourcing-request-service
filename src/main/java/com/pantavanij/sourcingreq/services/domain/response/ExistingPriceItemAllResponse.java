package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemDtoV2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class ExistingPriceItemAllResponse {
    private List<ExistingPriceItemDtoV2> data;
    private ApiResponseStatus status;

    public ExistingPriceItemAllResponse(List<ExistingPriceItemDtoV2> data) {
        this.data = data;
        this.status = new ApiResponseStatus();
    }

    public ExistingPriceItemAllResponse(List<ExistingPriceItemDtoV2> data, ApiResponseStatus status) {
        this.data = data;
        this.status = status;
    }
}
