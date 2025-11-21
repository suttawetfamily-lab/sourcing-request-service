package com.pantavanij.sourcingreq.services.enums;

public enum SearchRequestType {
    REQUEST_NO("requestNo"),
    REQUEST_NAME("requestName"),
    PROJECT_CODE("projectCode"),
    PROJECT_NAME("projectName"),
    PROJECT_LABEL("projectLabel"),
    DEPARTMENT_NAME("departmentName"),
    REQUESTER("createdBy"),
    REQUESTER_NAME("createdByName"),
    APPROVAL_TYPE("approvalType"),
    EXCEPTIONAL_SOURCING_DOC_NO("excSourcingDocNo"),
    SEQUENCE("sequence");

    private final String description;

    SearchRequestType(String description) {
        this.description = description;
    }


    public String description() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.name().concat(": ").concat(this.description);
    }

    public static String description(String name) {
        for (SearchRequestType statusCodeEnum : SearchRequestType.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}
