package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.UnitDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnitListResponse {
    private ApiResponseStatus status;

    private List<UnitDto> unitList;

    public UnitListResponse(List<UnitDto> unitList) {
        this.unitList = unitList;
        this.status = new ApiResponseStatus();
    }
}
