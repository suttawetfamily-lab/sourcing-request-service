package com.pantavanij.sourcingreq.services.domain.response.requester;

import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestSearchDataDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequesterRequestResponse {
    private List<RequesterRequestSearchDataDto> data;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
