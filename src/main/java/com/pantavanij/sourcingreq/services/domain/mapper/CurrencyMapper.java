package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(target = "value", source = "currency.recId")
    @Mapping(target = "name", source = "currency.code")
    @Mapping(target = "label", source = "currency.name")
    OptionDto toCurrencyOptionDto(Currency currency);

    List<OptionDto> toCurrencyOptionDto(List<Currency> currencies);

    @Mapping(target = "value", source = "currency.recId")
    @Mapping(target = "name", source = "currency.code")
    @Mapping(target = "label", source = "currency.name")
    CurrencyDto currencyToCurrencyDto(Currency currency);

    CurrencyMasterDto toCurrencyMasterDto(Currency currency);

    default CurrencyMasterDto toCurrencyMasterDto(Currency currency, String timeZone) {
        CurrencyMasterDto currencyMasterDto = toCurrencyMasterDto(currency);
        if (currencyMasterDto != null) {
            currencyMasterDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(currency.getCreatedDate(), timeZone));
            currencyMasterDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(currency.getUpdatedDate(), timeZone));
        }
        return currencyMasterDto;
    }

    default List<CurrencyDto> toCurrencyDto(List<Currency> currencyList, String timeZone) {
        List<CurrencyDto> currencyDtoList = new ArrayList<>();
        if (currencyList != null && !currencyList.isEmpty()) {
            currencyList.forEach(i -> currencyDtoList.add(currencyToCurrencyDto(i)));
        }
        return currencyDtoList;
    }

    default List<CurrencyMasterDto> toCurrencyMasterDto(List<Currency> currencyList, String timeZone) {
        List<CurrencyMasterDto> currencyMasterDtoList = new ArrayList<>();
        if (currencyList != null && !currencyList.isEmpty()) {
            currencyList.forEach(i -> currencyMasterDtoList.add(toCurrencyMasterDto(i, timeZone)));
        }
        return currencyMasterDtoList;
    }
}
