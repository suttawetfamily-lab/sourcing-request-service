package com.pantavanij.sourcingreq.services.enums;

public enum ApprovalStatus {
    APPROVAL_DRAFT(1),
    APPROVAL_AWAITING(2),
    APPROVAL_PARTIAL_COMPLETED(3),
    APPROVAL_COMPLETED(4),
    APPROVAL_REJECTED(5),
    APPROVAL_CANCELLED(6),
    APPROVAL_PENDING(7);

    private final Integer id;

    ApprovalStatus(Integer id) {
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }
}
