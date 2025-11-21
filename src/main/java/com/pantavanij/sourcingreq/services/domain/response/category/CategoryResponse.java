package com.pantavanij.sourcingreq.services.domain.response.category;

import com.pantavanij.sourcingreq.services.domain.dto.category.CategorySearchDataDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestSearchDataDto;
import com.pantavanij.sourcingreq.services.domain.response.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private ApiResponseStatus status;
    private List<CategorySearchDataDto> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
