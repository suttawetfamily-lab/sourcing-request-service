package com.pantavanij.sourcingreq.services.domain.dto.category;

import com.fasterxml.jackson.annotation.*;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import lombok.Data;
import java.sql.Timestamp;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto {
    private Integer recId;
    private String code;
    private String name;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private String createdByName;
    private String updatedByName;
    private List<PurchaserDto> purchaserList;
    private List<SubCategoryDto> subCategoryList;
}
