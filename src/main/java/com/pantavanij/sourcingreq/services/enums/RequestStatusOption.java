package com.pantavanij.sourcingreq.services.enums;

public enum RequestStatusOption {
    ALL_STATUS(0, "All Status", "All Status", true),
    SELECT_STATUS(1, "Selected Status", "Selected Status", false);

    private final Integer value;
    private final String code;
    private final String description;

    private boolean isDefault;

    RequestStatusOption(Integer value, String code, String description,boolean isDefault) {
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
