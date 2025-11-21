package com.pantavanij.sourcingreq.services.enums;

public enum ProjectSearchType {
    CODE("code"),
    NAME("name"),
    SEQUENCE("sequence"),
    CREATED_BY("createdBy")

    ;

    private final String description;

    ProjectSearchType(String description) { this.description = description; }

    public String description() { return this.description; }

    public static String description(String name) {
        for (ProjectSearchType statusCodeEnum : ProjectSearchType.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}
