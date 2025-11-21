package com.pantavanij.sourcingreq.services.enums;

public enum DeptApprovalStatus {
    DEPT_APPROVAL_NONE(1,  "None"),
    DEPT_APPROVAL_AWAITING(2, "Awaiting"),
    DEPT_APPROVAL_APPROVED(3, "Approved"),
    DEPT_APPROVAL_REJECTED(4, "Rejected"),
    DEPT_APPROVAL_CANCELLED(5, "Cancelled"),
    DEPT_APPROVAL_PENDING(6, "Pending");

    private final Integer id;
    private final String code;

    DeptApprovalStatus(Integer id,String code) {
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
