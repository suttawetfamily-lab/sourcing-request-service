package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExistingPriceItemListResponse {
    private List<ExistingPriceItemDto> data;
    private ApiResponseStatus status;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;

    public ExistingPriceItemListResponse(List<ExistingPriceItemDto> data, int page, int pageSize, long total, long totalPage) {
        this.data = data;
        this.status = new ApiResponseStatus();
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPage = totalPage;
    }

    public ExistingPriceItemListResponse(List<ExistingPriceItemDto> data, ApiResponseStatus status) {
        this.data = data;
        this.status = status;
    }
}
