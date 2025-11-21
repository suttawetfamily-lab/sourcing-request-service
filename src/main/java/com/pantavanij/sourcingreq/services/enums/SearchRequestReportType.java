package com.pantavanij.sourcingreq.services.enums;

public enum SearchRequestReportType {
    NAME("name"),
    CODE("code"),
    CREATED_BY("createdBy"),
    ;

    private final String description;

    SearchRequestReportType(String description) { this.description = description; }

    public String description() { return this.description; }

    public static String description(String name) {
        for (SearchRequestReportType statusCodeEnum : SearchRequestReportType.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}

