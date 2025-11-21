package com.pantavanij.sourcingreq.services.enums;

public enum SearchDepartmentType {
    NAME("name"),
    CODE("code"),
    CREATED_BY("createdBy")
    ;

    private final String description;

    SearchDepartmentType(String description) { this.description = description; }

    public String description() { return this.description; }

    public static String description(String name) {
        for (SearchDepartmentType statusCodeEnum : SearchDepartmentType.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }

}
