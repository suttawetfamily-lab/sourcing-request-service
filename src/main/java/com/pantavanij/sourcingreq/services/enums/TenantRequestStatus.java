package com.pantavanij.sourcingreq.services.enums;

public enum TenantRequestStatus {
    TENANT_REQUEST_DRAFT("Draft"),
    TENANT_REQUEST_AWAITING("Awaiting"),
    TENANT_REQUEST_PARTIAL_COMPLETED("Partial Completed"),
    TENANT_REQUEST_COMPLETED("Completed"),
    TENANT_REQUEST_REJECTED("Rejected"),
    TENANT_REQUEST_CANCELLED("Cancelled"),
    TENANT_REQUEST_PENDING("Pending");

    private final String code;

    TenantRequestStatus(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }
}
