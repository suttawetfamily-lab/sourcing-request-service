package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDetailDto;
import com.pantavanij.sourcingreq.services.domain.dto.PurchaserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Category;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purchaser;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SubCategory;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface PurchaserMapper {
    PurchaserMapper INSTANCE = Mappers.getMapper(PurchaserMapper.class);

    @Mapping(target = "value", source = "purchaser.recId")
    @Mapping(target = "name", source = "purchaser.loginId")
    @Mapping(target = "label", source = "purchaser.purchaserName")
    @Mapping(target = "email", source = "purchaser.email")
    @Mapping(target = "phone", source = "purchaser.phone")
    OptionDetailDto toPurchaserOptionDetailDto(Purchaser purchaser);
    List<OptionDetailDto> toPurchaserOptionDetailDto(List<Purchaser> purchasers);
    List<PurchaserDto> toPurchaserDtoList(List<Purchaser> purchasers);
    PurchaserDto toPurchaserDto(Purchaser purchaser);

    default PurchaserDto toPurchaserDto(Purchaser purchaser, String timeZone) {
        PurchaserDto purchaserDto = toPurchaserDto(purchaser);
        if (purchaserDto != null ) {
            purchaserDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(purchaser.getCreatedDate(), timeZone));
            purchaserDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(purchaser.getUpdatedDate(), timeZone));

            List<Category> categoryList = purchaser.getCategoryPurchasers().stream()
                    .map(c -> {
                        Category category = c.getCategory();
                        category.setPurchaserList(null);
                        category.setSubCategoryList(null);
                        return category;
                    })
                    .collect(Collectors.toList());
            purchaserDto.setCategoryList(CategoryMapper.INSTANCE.toCategoryDtoList(categoryList));

            List<SubCategory> subCategoryList = purchaser.getSubCategoryPurchasers().stream()
                    .map(sc -> {
                        SubCategory subCategory = sc.getSubCategory();
                        subCategory.setPurchaserList(null);
                        return subCategory;
                    })
                    .collect(Collectors.toList());
            purchaserDto.setSubCategoryList(SubCategoryMapper.INSTANCE.toSubCategoryDtoList(subCategoryList));
        }
        return purchaserDto;
    }

}
