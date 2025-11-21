package com.pantavanij.sourcingreq.services.enums;

public enum TenantApprovalStatus {
    TENANT_APPROVAL_DRAFT("Draft"),
    TENANT_APPROVAL_AWAITING("Awaiting"),
    TENANT_APPROVAL_PARTIAL_COMPLETED("Partial Completed"),
    TENANT_APPROVAL_COMPLETED("Completed"),
    TENANT_APPROVAL_REJECTED("Rejected"),
    TENANT_APPROVAL_CANCELLED("Cancelled"),
    TENANT_APPROVAL_PENDING("Pending");

    private final String code;

    TenantApprovalStatus(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }
}
