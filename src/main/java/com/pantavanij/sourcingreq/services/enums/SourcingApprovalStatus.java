package com.pantavanij.sourcingreq.services.enums;

public enum SourcingApprovalStatus {
    SOURCING_APPROVAL_NONE(0,  "None"),
    SOURCING_APPROVAL_PENDING(1, "Pending"),
    SOURCING_APPROVAL_AWAITING(2, "Awaiting"),
    SOURCING_APPROVAL_APPROVED(3, "Approved"),
    SOURCING_APPROVAL_REJECTED(4, "Rejected"),
    SOURCING_APPROVAL_CANCELLED(5, "Cancelled");

    private final Integer id;
    private final String code;

    SourcingApprovalStatus(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public String code() {
        return this.code;
    }

    public Integer id() {
        return this.id;
    }
}
