package com.pantavanij.sourcingreq.services.enums;

public enum PathUrl {
    SOURCING_REQUEST("sourcing-request", "SQN"),
    APPROVE("manage-request", "SQP"),
    REVIEW_REQUEST("review-request", "SQV"),
    APPROVE_REQUEST("approve-request", "SQA"),
    APPROVE_SOURCING("approve-sourcing", "SQX");
    // PURCHASE_APPROVE_SOURCING("purchase-approve-sourcing", "SQP");

    private final String privilegeCode;
    private final String path;

    PathUrl(String path, String privilegeCode) {
        this.path = path;
        this.privilegeCode = privilegeCode;
    }

    public String path() {
        return this.path;
    }

    public String privilegeCode() {
        return this.privilegeCode;
    }

    public static PathUrl findByStrPath(String path) {
        PathUrl result = null;
        for (PathUrl pathUrl : values()) {
            if (pathUrl.path.equalsIgnoreCase(path) || pathUrl.name().equalsIgnoreCase(path)) {
                result = pathUrl;
                break;
            }
        }
        return result;
    }

}
