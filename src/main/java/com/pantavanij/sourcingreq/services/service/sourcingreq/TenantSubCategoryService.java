package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSubCategoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantCategory;

import java.util.List;

public interface TenantSubCategoryService {

    List<TenantSubCategoryDto> getSubCategoryByTenantIdAndTypeAndBuyer(Integer tenantId, Integer typeId, String purchaser);

    List<TenantSubCategoryDto> getSubCategoryByTenantIdAndType(Integer tenantId, Integer typeId);

    List<OptionDto> getSubCategoryByTenantIdOptionDto(Integer tenantId, Integer typeId);

    TenantCategory findBySubCategoryId(Long subCategoryId);

    TenantSubCategoryDto findById(Long subCategoryId);

}
