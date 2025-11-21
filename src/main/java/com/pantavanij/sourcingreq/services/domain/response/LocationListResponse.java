package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationListResponse {
    private ApiResponseStatus status;

    private List<LocationDto> locationList;

    public LocationListResponse(List<LocationDto> locationList) {
        this.locationList = locationList;
        this.status = new ApiResponseStatus();
    }
}
