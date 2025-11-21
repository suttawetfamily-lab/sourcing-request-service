package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Unit;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;

@Mapper
public interface UnitMapper {

    UnitMapper INSTANCE = Mappers.getMapper(UnitMapper.class);

    List<UnitDto> toUnitDto(List<Unit> units);

    List<OptionDto> toUnitOptionDto(List<Unit> units);

    @Mapping(target = "unitId", source = "unit.recId")
    @Mapping(target = "unitCode", source = "unit.code")
    @Mapping(target = "description", source = "unit.name")
    UnitDto toUnitDto(Unit unit);

    @Mapping(target = "value", source = "unit.recId")
    @Mapping(target = "name", source = "unit.code")
    @Mapping(target = "label", source = "unit.name")
    OptionDto toUnitOptionDto(Unit unit);

    @Mapping(target = "value", source = "unit.recId")
    @Mapping(target = "name", source = "unit.code")
    @Mapping(target = "label", source = "unit.name")
    OptionDto toUnitOptionDto(Unit unit, String timeZone);

    OptionDto toUnitOptionDto(OptionDto optionDto, String timeZone);

    UnitMasterDataDto toUnitMasterDataDto(Unit unit);

    List<UnitMasterDataDto> toUnitMasterDataDtoList(List<Unit> unitList);


    default UnitMasterDataDto toUnitMasterDataDto(Unit unit, String timeZone) {
        UnitMasterDataDto unitMasterDataDto = toUnitMasterDataDto(unit);
        if (unitMasterDataDto != null) {
            unitMasterDataDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(unitMasterDataDto.getCreatedDate(), timeZone));
            unitMasterDataDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(unitMasterDataDto.getUpdatedDate(), timeZone));
        }
        return unitMasterDataDto;
    }

    default List<UnitMasterDataDto> toUnitMasterDataDtoList(List<Unit> unitList, String timeZone) {
        List<UnitMasterDataDto> unitMasterDataDtoList = new ArrayList<>();
        if (!unitList.isEmpty()) {
            unitList.forEach(i -> unitMasterDataDtoList.add(toUnitMasterDataDto(i, timeZone)));
        }
        return unitMasterDataDtoList;
    }

    default List<OptionDto> toUnitOptionDtoList(List<Unit> unitList, String timeZone) {
        List<OptionDto> optionDtoList = new ArrayList<>();
        if (!unitList.isEmpty()) {
            unitList.forEach(i -> optionDtoList.add(toUnitOptionDto(i, timeZone)));
        }
        return optionDtoList;
    }


}
