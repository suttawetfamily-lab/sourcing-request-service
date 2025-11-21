package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class TenantCurrencyOptionDto {
    private String code;
    private String name;
    private Integer sequence;
    private Boolean active;
    private Boolean isDefault;

    public TenantCurrencyOptionDto(String code, String name, Integer sequence, Boolean active, Boolean isDefault) {
        this.code = code;
        this.name = name;
        this.sequence = sequence;
        this.active = active;
        this.isDefault = isDefault;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public Integer getSequence() { return sequence; }
    public Boolean getActive() { return active; }
    public Boolean getIsDefault() { return isDefault; }
}
