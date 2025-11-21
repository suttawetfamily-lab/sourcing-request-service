package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.*;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import lombok.Data;
import java.sql.Timestamp;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaserDto {
    private Integer recId;
    private Integer userId;
    private String loginId;
    private String purchaserName;
    private String email;
    private String phone;
    private Integer roleId;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<PurchaserCategoryDto> categoryPurchaserList;
    private List<PurchaserSubCategoryDto> subCategoryPurchaserList;
    private List<CategoryDto> categoryList;
    private List<SubCategoryDto> subCategoryList;
}
