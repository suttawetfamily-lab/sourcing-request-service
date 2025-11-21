package com.pantavanij.sourcingreq.services.enums;

public enum SearchRequestItemReportType {
    NAME("name"),
    CODE("code"),
    CREATED_BY("createdBy"),
    ;

    private final String description;

    SearchRequestItemReportType(String description) { this.description = description; }

    public String description() { return this.description; }

    public static String description(String name) {
        for (SearchRequestItemReportType statusCodeEnum : SearchRequestItemReportType.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}

