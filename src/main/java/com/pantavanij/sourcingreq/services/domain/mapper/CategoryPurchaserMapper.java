package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

import java.util.*;

@Mapper
public interface CategoryPurchaserMapper {
    CategoryPurchaserMapper INSTANCE = Mappers.getMapper(CategoryPurchaserMapper.class);
    CategoryPurchaserDto toCategoryPurchaserDto(CategoryPurchaser categoryPurchaser);
    List<CategoryPurchaserDto> toCategoryPurchaserDtoList(List<CategoryPurchaser> categoryPurchasers);
}
