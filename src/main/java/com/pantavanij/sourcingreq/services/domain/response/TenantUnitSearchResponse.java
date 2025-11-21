package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import lombok.*;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantUnitSearchResponse {
    private ApiResponseStatus status;
    private List<TenantUnitDto> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
