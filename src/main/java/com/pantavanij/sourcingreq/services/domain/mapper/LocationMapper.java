package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);
    @Mapping(target = "value", source = "location.recId")
    @Mapping(target = "label", source = "location.name")
    @Mapping(target = "name", source = "location.name")
    @Mapping(target = "isDefault", source = "location.default")
    List<LocationDto> toLocationDtoList(List<Location> location);
    @Mapping(target = "value", source = "location.recId")
    @Mapping(target = "label", source = "location.name")
    @Mapping(target = "name", source = "location.name")
    @Mapping(target = "isDefault", source = "location.default")
    LocationDto toLocationDto(Location location);

}
