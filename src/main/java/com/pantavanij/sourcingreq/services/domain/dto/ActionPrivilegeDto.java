package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActionPrivilegeDto {
    private Integer recId;
    private String action;
    private Integer typeId;
    private String privilegeCode;
}
