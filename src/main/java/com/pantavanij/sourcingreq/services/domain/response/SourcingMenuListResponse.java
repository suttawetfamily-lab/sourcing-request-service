package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingMenuDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourcingMenuListResponse {
    private ApiResponseStatus status;

    private List<SourcingMenuDto> sourcingMenuList;

    public SourcingMenuListResponse(List<SourcingMenuDto> sourcingMenuList) {
        this.sourcingMenuList = sourcingMenuList;
        this.status = new ApiResponseStatus();
    }
}
