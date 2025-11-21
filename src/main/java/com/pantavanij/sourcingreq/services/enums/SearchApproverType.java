package com.pantavanij.sourcingreq.services.enums;

public enum SearchApproverType {
    APPROVER_NAME("approverName"),
    EMAIL("email"),
    CREATED_BY("createdBy"),
    LOGIN_ID("loginId")

    ;

    private final String description;

    SearchApproverType(String description) { this.description = description; }

    public String description() { return this.description; }

    public static String description(String name) {
        for (SearchApproverType statusCodeEnum : SearchApproverType.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}

