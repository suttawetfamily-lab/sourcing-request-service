package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ExistingPriceItemDtoV2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExistingPriceItemListResponseV2 {
    private List<ExistingPriceItemDtoV2> data;
    private ApiResponseStatus status;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;

    public ExistingPriceItemListResponseV2(List<ExistingPriceItemDtoV2> data, int page, int pageSize, long total, long totalPage) {
        this.data = data;
        this.status = new ApiResponseStatus();
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPage = totalPage;
    }

    public ExistingPriceItemListResponseV2(List<ExistingPriceItemDtoV2> data, ApiResponseStatus status) {
        this.data = data;
        this.status = status;
    }
}
