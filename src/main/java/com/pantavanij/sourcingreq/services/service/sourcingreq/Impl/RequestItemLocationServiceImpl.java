package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Location;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemLocation;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemLocationKey;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemLocationRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestItemLocationServiceImpl implements RequestItemLocationService {

    private final RequestItemLocationRepository requestItemLocationRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(LocationDto locationDto, RequestItem requestItem) {
        Optional<RequestItemLocation> existingRequestItemLocation =
                requestItemLocationRepository.findTop1ByRequestItemId(requestItem.getRecId());
        if (existingRequestItemLocation.isPresent()) {
            requestItemLocationRepository.delete(existingRequestItemLocation.get());
        }
        if (locationDto == null || locationDto.getValue() == null) return;
        Location location = new Location();
        location.setRecId(Integer.parseInt(locationDto.getValue()));
        RequestItemLocation requestItemLocation = RequestItemLocation.builder()
                .id(new RequestItemLocationKey())
                .location(location)
                .requestItem(requestItem)
                .deliveryLocation(locationDto.getAddress())
                .contactName(locationDto.getContactName())
                .phone(locationDto.getPhone())
                .build();
        requestItemLocationRepository.save(requestItemLocation);
    }
}
