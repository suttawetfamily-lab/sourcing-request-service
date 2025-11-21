package com.pantavanij.sourcingreq.services.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConditionSearchRequest {
    private String searchField;
    private String searchValue;
}
