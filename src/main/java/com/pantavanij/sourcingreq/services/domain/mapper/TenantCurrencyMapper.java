package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.domain.projection.TenantCurrencyOptionProjection;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

import java.util.*;

@Mapper
public interface TenantCurrencyMapper {
    TenantCurrencyMapper INSTANCE = Mappers.getMapper(TenantCurrencyMapper.class);

    TenantCurrencyDto toTenantCurrencyDto(TenantCurrency tenantCurrency);
    List<TenantCurrencyDto> toTenantCurrencyDtoList(List<TenantCurrency> tenantCurrencyList);

    default TenantCurrencyDto toTenantCurrencyDto(TenantCurrency tenantCurrency, String timeZone) {
        TenantCurrencyDto tenantCurrencyDto = toTenantCurrencyDto(tenantCurrency);
        if (tenantCurrencyDto != null) {
            Currency currency = tenantCurrency.getCurrency();
            if (tenantCurrencyDto.getCurrencyObj() == null) {
                CurrencyDto currencyDto = new CurrencyDto();
                currencyDto.setLabel(currency.getCode());
                currencyDto.setName(currency.getName());
                currencyDto.setValue(currency.getRecId().toString());
                tenantCurrencyDto.setCurrencyObj(currencyDto);
            }
            tenantCurrencyDto.setDefault(tenantCurrency.isDefault());
            tenantCurrencyDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantCurrencyDto.getCreatedDate(), timeZone));
            tenantCurrencyDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(tenantCurrencyDto.getUpdatedDate(), timeZone));

        }
        return tenantCurrencyDto;
    }

    default List<TenantCurrencyDto> toTenantCurrencyDtoList(List<TenantCurrency> tenantCurrencyList, String timeZone) {
        List<TenantCurrencyDto> tenantCurrencyDtoList = new ArrayList<>();
        if (!tenantCurrencyList.isEmpty()) {
            tenantCurrencyList.forEach(i -> tenantCurrencyDtoList.add(toTenantCurrencyDto(i, timeZone)));
        }
        return tenantCurrencyDtoList;
    }

    @Mapping(source = "value", target = "value")
    @Mapping(source = "code", target = "name")
    @Mapping(source = "name", target = "label")
    @Mapping(source = "isDefault", target = "isDefault")
    OptionDto toOptionDto(TenantCurrencyOptionProjection tenantCurrency);

    List<OptionDto> toOptionDtoList(List<TenantCurrencyOptionProjection> tenantCurrencyList);
}
