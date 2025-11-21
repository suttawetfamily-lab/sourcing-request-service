package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitSearchResponse {
    private ApiResponseStatus status;
    private List<UnitMasterDataDto> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
