package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategorySearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.Pageable;

import javax.validation.*;
import java.util.List;

public interface CategoryService {

    List<OptionDto> getCategoryByTenantIdAndSearchTerm(Integer tenantId, String searchTerm, Integer organizationId);

    List<CategoryDto> getCategoryByTenant(Integer tenantId);

    CategorySearchDto searchCategoryByCondition(CategorySearchRequest searchRequest, Pageable pageable);

    CategoryDto findCategoryByRecId(Integer recId);

    CategoryDto saveCategory(CategoryRequest request);

    CategoryDto updateCategory(CategoryRequest request, List<Purchaser> purchasers);

    int deleteCategory(Integer categoryId);

    CategoryDto updateCategorySequence(SequenceRequest request);
}
