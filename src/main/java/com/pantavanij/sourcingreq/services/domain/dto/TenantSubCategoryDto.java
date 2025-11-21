package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantSubCategoryDto {
    private Long value;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String label;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String buyer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String telephone;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String categoryName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long categoryId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayPurchaser;

}
