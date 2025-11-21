package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import java.util.List;
public interface LocationService {

    List<LocationDto> getLocationByTenantId(Integer tenantId, Integer organizationId);
    LocationDto getLocationById(Integer recId);
}
