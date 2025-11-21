package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.SubCategoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;

import java.util.*;

public interface SubCategoryService {

    List<OptionDto> getSubCategoryByTenantIdAndSearchTermAndCategoryId(Integer tenantId, String searchTerm, Integer categoryId);

    List<SubCategoryDto> getSubCategoryByTenantId(Integer tenantId);

    List<OptionDto> getSubCategoryOptionDtoByTenantId(Integer tenantId);

    SubCategoryDto createSubCategory(SubCategoryRequest request);

    int deleteSubCategoryByRecId(Integer id, boolean isReOrderSequence);

    SubCategoryDto updateSubCategorySequence(SequenceRequest request);

    SubCategoryDto getSubCategoryById(Integer subCategoryId);

    SubCategoryDto updateSubCategory(SubCategoryRequest request);
}
