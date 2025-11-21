package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Location;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.LocationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Before
    public void setup() {

    }

    @Test
    public void findLocationByTenantId_success() {
        List<Location> locationOptional = new ArrayList<>();
        List<LocationDto> locationDtoOptional = null;

        Mockito.when(locationRepository.getLocationByTenantId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(locationOptional);
        Assert.assertEquals(locationDtoOptional, locationService.getLocationByTenantId(Mockito.anyInt(), Mockito.anyInt()));
    }
}
