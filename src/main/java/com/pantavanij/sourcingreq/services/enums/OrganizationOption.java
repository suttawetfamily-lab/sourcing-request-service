package com.pantavanij.sourcingreq.services.enums;

public enum OrganizationOption {
    ALL_ORGANIZATION(0, "All Organization", "All Organization", true),
    SELECT_ORGANIZATION(1, "Selected Organization", "Selected Organization", false);

    private final Integer value;
    private final String code;
    private final String description;

    private boolean isDefault;

    OrganizationOption(Integer value, String code, String description, boolean isDefault) {
        this.value = value;
        this.code = code;
        this.description = description;
        this.isDefault = isDefault;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean getIsDefault() { return isDefault; }
}
