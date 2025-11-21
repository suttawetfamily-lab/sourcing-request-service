package com.pantavanij.sourcingreq.services.enums;

public enum SearchRequester {
    REC_ID("recId"),
    REQUESTER_NAME("requesterName"),
    EMAIL("email"),
    CREATED_BY("createdBy"),
    USER_ID("userId"),
    LOGIN_ID("loginId")

    ;

    private final String description;

    SearchRequester(String description) {
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
        for (SearchRequester statusCodeEnum : SearchRequester.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}
