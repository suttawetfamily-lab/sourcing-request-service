package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSubCategoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSubCategory;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantSubCategoryMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantSubCategoryRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSubCategoryService;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantSubCategoryServiceImpl implements TenantSubCategoryService {

    private final TenantSubCategoryRepository tenantSubCategoryRepository;

    @Override
    public List<TenantSubCategoryDto> getSubCategoryByTenantIdAndType(Integer tenantId, Integer typeId) {
        List<TenantSubCategory> subCategories = tenantSubCategoryRepository.getSubcategoryByTenantIdAndTypeId(tenantId, typeId, "");
        return TenantSubCategoryMapper.INSTANCE.toTenantSubCategoryDtoList(subCategories);
    }

    @Override
    public List<TenantSubCategoryDto> getSubCategoryByTenantIdAndTypeAndBuyer(Integer tenantId, Integer typeId, String purchaser) {
        List<TenantSubCategory> subCategories = tenantSubCategoryRepository.getSubcategoryByTenantIdAndTypeId(tenantId, typeId, purchaser);

        Map<String, String> buyerFullName = new HashMap<>();
        List<String> buyerNames = subCategories.stream().map(TenantSubCategory::getBuyer).distinct().collect(Collectors.toList());
        for (String buyer : buyerNames) {
            if (buyer != null && !buyer.trim().isEmpty()) {
                buyerFullName.put(buyer, UserDetailServiceUtil.getFullName(buyer));
            }
        }

        return TenantSubCategoryMapper.INSTANCE.toTenantSubCategoryDtoList(subCategories)
                .stream().peek(it -> it.setDisplayPurchaser(null != buyerFullName.get(it.getBuyer()) ? buyerFullName.get(it.getBuyer()) : "")).collect(Collectors.toList());
    }

    @Override
    public List<OptionDto> getSubCategoryByTenantIdOptionDto(Integer tenantId, Integer typeId) {
        List<TenantSubCategory> subCategories = tenantSubCategoryRepository.getSubcategoryByTenantId(tenantId, typeId);
        return TenantSubCategoryMapper.INSTANCE.toTenantSubCategoryOptionDto(subCategories);
    }

    @Override
    public TenantCategory findBySubCategoryId(Long subCategoryId) {
        Optional<TenantSubCategory> tenantSubCategory =  tenantSubCategoryRepository.findById(subCategoryId);
        return tenantSubCategory.map(TenantSubCategory::getCategory).orElse(null);
    }

    @Override
    public TenantSubCategoryDto findById(Long subCategoryId) {
        TenantSubCategoryDto tenantSubCategoryDto = null;
        Optional<TenantSubCategory> tenantSubCategory =  tenantSubCategoryRepository.findById(subCategoryId);
        if (tenantSubCategory.isPresent()) {
            tenantSubCategoryDto = TenantSubCategoryMapper.INSTANCE.toTenantSubCategoryDto(tenantSubCategory.get());
        }
        return tenantSubCategoryDto;
    }

}
