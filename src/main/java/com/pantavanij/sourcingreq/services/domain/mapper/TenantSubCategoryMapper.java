package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSubCategoryDto;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.ReviewerRequestDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TenantSubCategoryMapper {
    TenantSubCategoryMapper INSTANCE = Mappers.getMapper(TenantSubCategoryMapper.class);

    @Mapping(target = "name", source = "subCategoryName")
    @Mapping(target = "code", source = "subCategoryCode")
    @Mapping(target = "categoryName", source = "category.categoryName")
    @Mapping(target = "value", source = "id")
    @Mapping(target = "label", source = "subCategoryName")
    @Mapping(target = "categoryId", source = "category.id")
    TenantSubCategoryDto toTenantSubCategoryDto(TenantSubCategory subCategory);

    List<TenantSubCategoryDto> toTenantSubCategoryDtoList(List<TenantSubCategory> subCategories);

    @Mapping(target = "value", source = "id")
    @Mapping(target = "name", source = "subCategoryName")
    @Mapping(target = "label", source = "subCategoryName")
    OptionDto toOptionDto(TenantSubCategory tenantSubCategory);

    default List<OptionDto> toTenantSubCategoryOptionDto(List<TenantSubCategory> tenantSubCategories) {
        List<OptionDto> optionDtos = new ArrayList();
        if (tenantSubCategories != null) {
            tenantSubCategories.forEach(i -> optionDtos.add(toOptionDto(i)));
        }
        return optionDtos;
    }
}
