package com.pantavanij.sourcingreq.services.enums;

public enum SearchReportLine {
    REC_ID("recId"),
    APPROVER_NAME("approverName"),
    EMAIL("email"),
    CREATED_BY("createdBy"),
    USER_ID("userId"),
    LOGIN_ID("loginId")

    ;

    private final String description;

    SearchReportLine(String description) {
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
        for (SearchReportLine statusCodeEnum : SearchReportLine.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}
