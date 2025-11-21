package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Location;
import com.pantavanij.sourcingreq.services.domain.mapper.LocationMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.LocationRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public List<LocationDto> getLocationByTenantId(Integer tenantId, Integer organizationId) {
        List<LocationDto> LocationDtoLst = null;

        List<Location> locationList = locationRepository.getLocationByTenantId(tenantId, organizationId);
        if(locationList.size() > 0) {
            LocationDtoLst = LocationMapper.INSTANCE.toLocationDtoList(locationList);
        }
        return LocationDtoLst;
    }
    public LocationDto getLocationById(Integer recId) {
        Location location = locationRepository.getById(recId);
        return LocationMapper.INSTANCE.toLocationDto(location);
    }
}
