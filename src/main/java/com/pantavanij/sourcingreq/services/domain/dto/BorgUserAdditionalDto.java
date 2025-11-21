package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BorgUserAdditionalDto {
    private Integer borgID;
    private String borgCode;
    private String borgName;

    public BorgUserAdditionalDto() {
    }

    public BorgUserAdditionalDto(Integer borgID, String borgCode, String borgName) {
        this.borgID = borgID;
        this.borgCode = borgCode;
        this.borgName = borgName;
    }
}
