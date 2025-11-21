package com.pantavanij.sourcingreq.services.enums;

public enum Role {
    REQUESTER("SQN", "Requester"),
    PURCHASER("SQP", "Purchaser"),
    DEPT_APPROVER("SQA","Approver"),
    REVIEWER("SQV", "Reviewer"),
    REPORT_LINE("SRL", "ReportLine"),
    EXC_DEPT_APPROVER("SQX", "Approver"),
    EXC_PURCHASING_APPROVER("SQE", "PurchasingApprover");

    private final String privilegeCode;
    private final String roleName;

    Role(String privilegeCode, String roleName) {
        this.privilegeCode = privilegeCode;
        this.roleName = roleName;
    }

    public String privilegeCode() {
        return this.privilegeCode;
    }

    public String roleName() {
        return this.roleName;
    }
}
