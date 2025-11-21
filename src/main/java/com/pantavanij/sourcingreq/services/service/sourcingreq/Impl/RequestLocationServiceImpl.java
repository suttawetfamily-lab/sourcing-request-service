package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Location;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestLocation;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestLocationKey;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestLocationRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestLocationServiceImpl implements RequestLocationService {
    private final RequestLocationRepository requestLocationRepository;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(LocationDto locationDto, Request request) {
        Optional<RequestLocation> existingRequestLocation =
                requestLocationRepository.findTop1ByRequestId(request.getRecId());
        if (existingRequestLocation.isPresent()) {
            requestLocationRepository.delete(existingRequestLocation.get());
        }
        if (locationDto == null || locationDto.getValue() == null) return;
        Location location = new Location();
        location.setRecId(Integer.parseInt(locationDto.getValue()));
        RequestLocation requestLocation = RequestLocation.builder()
                    .id(new RequestLocationKey())
                    .location(location)
                    .request(request)
                    .deliveryLocation(locationDto.getAddress())
                    .contactName(locationDto.getContactName())
                    .phone(locationDto.getPhone())
                    .build();
        requestLocationRepository.save(requestLocation);
    }
}
