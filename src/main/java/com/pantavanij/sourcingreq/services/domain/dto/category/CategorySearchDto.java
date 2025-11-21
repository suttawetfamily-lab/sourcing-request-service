package com.pantavanij.sourcingreq.services.domain.dto.category;

import lombok.Data;
import java.util.List;

@Data
public class CategorySearchDto {
    private List<CategoryDto> categoryDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
