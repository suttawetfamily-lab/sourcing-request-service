package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestGridFieldSearchResponse {
    private ApiResponseStatus status;
    private List<RequestGridFieldDto> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
