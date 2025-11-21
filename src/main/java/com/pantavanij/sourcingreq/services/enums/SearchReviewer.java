package com.pantavanij.sourcingreq.services.enums;

public enum SearchReviewer {
    REC_ID("recId"),
    REVIEWER_NAME("reviewerName"),
    EMAIL("email"),
    CREATED_BY("createdBy"),
    USER_ID("userId"),
    LOGIN_ID("loginId")

    ;

    private final String description;

    SearchReviewer(String description) {
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
        for (SearchReviewer statusCodeEnum : SearchReviewer.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}
