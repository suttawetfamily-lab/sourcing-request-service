package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class CheckPrivilegeDto {
    private String privilegeCode;
    private Boolean isPrivilege;
    private Long scopeId;
}
