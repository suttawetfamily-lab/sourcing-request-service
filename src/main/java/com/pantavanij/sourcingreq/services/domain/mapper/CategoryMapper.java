package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategorySearchDataDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.*;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDtoList(List<Category> category);
    @Mapping(target = "value", source = "category.recId")
    @Mapping(target = "name", source = "category.code")
    @Mapping(target = "label", source = "category.name")
    OptionDto toCategoryOptionDto(Category category);

    List<OptionDto> toCategoryOptionDto(List<Category> categories);

    @Mapping(target = "value", source = "tenantCategory.id")
    @Mapping(target = "name", source = "tenantCategory.categoryName")
    @Mapping(target = "label", source = "tenantCategory.categoryName")
    OptionDto toTenantCategoryDto(TenantCategory tenantCategory);

    CategorySearchDataDto toCategorySearchDataDto(CategoryDto categoryDto);

    List<CategorySearchDataDto> toCategorySearchDataDto(List<CategoryDto> categoryDtos);


    default CategoryDto toCategoryDto(Category category, String timeZone) {
        CategoryDto categoryDto = toCategoryDto(category);
        if (categoryDto != null) {
            if (!category.getPurchaserList().isEmpty()) {
                List<Purchaser> purchaserList = category.getPurchaserList().stream()
                        .map(CategoryPurchaser::getPurchaser)
                        .collect(Collectors.toList());

                purchaserList = purchaserList.stream()
                        .sorted(Comparator.comparing(Purchaser::getSequence))
                        .collect(Collectors.toList());

                categoryDto.setPurchaserList(
                        PurchaserMapper.INSTANCE.toPurchaserDtoList(purchaserList));
            }
            categoryDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(categoryDto.getCreatedDate(), timeZone));
            categoryDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(categoryDto.getUpdatedDate(), timeZone));
        }
        return categoryDto;
    }

    default List<CategoryDto> toCategoryDtoList(List<Category> categoryList, String timeZone) {
        List<CategoryDto> categoryDtoList = new ArrayList();
        if(categoryList != null) {
            categoryList.forEach(i -> categoryDtoList.add(toCategoryDto(i, timeZone)));
        }
        return categoryDtoList;
    }

}
