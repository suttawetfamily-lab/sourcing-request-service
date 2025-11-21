package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import lombok.*;

import java.util.*;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class EmailActivitySearchResponse {
    private ApiResponseStatus status;
    private List<EmailActivityDto> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
