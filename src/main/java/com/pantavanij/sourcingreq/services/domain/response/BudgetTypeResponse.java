package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetTypeResponse {
    private ApiResponseStatus status;

    private List<OptionDto> budgetTypeList;

    public BudgetTypeResponse(List<OptionDto> budgetTypeList) {
        this.budgetTypeList = budgetTypeList;
        this.status = new ApiResponseStatus();
    }
}
