package com.pantavanij.sourcingreq.services.enums;

public enum SearchPurchaserType {
    PURCHASER_NAME("purchaserName"),
    EMAIL("email"),
    CREATED_BY("createdBy"),
    LOGIN_ID("loginId")
    ;

    private final String description;

    SearchPurchaserType(String description) { this.description = description; }

    public String description() { return this.description; }

    public static String description(String name) {
        for (SearchPurchaserType statusCodeEnum : SearchPurchaserType.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}

