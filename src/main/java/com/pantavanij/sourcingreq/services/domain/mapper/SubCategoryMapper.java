package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.SubCategoryDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.*;

@Mapper
public interface SubCategoryMapper {
    SubCategoryMapper INSTANCE = Mappers.getMapper(SubCategoryMapper.class);
    @Mapping(target = "categoryId",source = "subCategory.category.recId")

    SubCategoryDto toSubCategoryDto(SubCategory subCategory);
    @Mapping(target = "categoryId",source = "subCategory.category.recId")
    List<SubCategoryDto> toSubCategoryDtoList(List<SubCategory> subCategories);

    @Mapping(target = "value", source = "subCategory.recId")
    @Mapping(target = "name", source = "subCategory.code")
    @Mapping(target = "label", source = "subCategory.name")
    OptionDto toSubCategoryOptionDto(SubCategory subCategory);

    List<OptionDto> toSubCategoryOptionDto(List<SubCategory> subCategories);

    default SubCategoryDto toSubCategoryDto(SubCategory subCategory, String timeZone) {
        SubCategoryDto subCategoryDto = toSubCategoryDto(subCategory);
        if (subCategoryDto != null) {
            if (!subCategory.getPurchaserList().isEmpty()) {
                List<Purchaser> purchaserList = subCategory.getPurchaserList().stream()
                        .map(SubCategoryPurchaser::getPurchaser)
                        .collect(Collectors.toList());

                purchaserList = purchaserList.stream()
                        .sorted(Comparator.comparing(Purchaser::getSequence))
                        .collect(Collectors.toList());

                subCategoryDto.setPurchaserList(PurchaserMapper.INSTANCE.toPurchaserDtoList(purchaserList));
            }
            subCategoryDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(subCategoryDto.getCreatedDate(), timeZone));
            subCategoryDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(subCategoryDto.getUpdatedDate(), timeZone));
        }
        return subCategoryDto;
    }
}
