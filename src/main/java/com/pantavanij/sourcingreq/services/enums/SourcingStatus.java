package com.pantavanij.sourcingreq.services.enums;

public enum SourcingStatus {
    SOURCING_NONE(1, "NONE"),
    SOURCING_DRAFT(2, "DRAFT"),
    SOURCING_AWAITING_RESPONSE(3, "AWAITING_RESPONSE"),
    SOURCING_PENDING(4, "PENDING"),
    SOURCING_CANCELLED(5, "CANCELLED"),
    SOURCING_AWAITING_ACTIVE(6, "AWAITING_ACTIVE"),
    SOURCING_AWAITING_SHORTLIST(7, "AWAITING_SHORTLIST"),
    SOURCING_AWAITING_APPROVE_SHORTLIST(8, "AWAITING_APPROVE_SHORTLIST"),
    SOURCING_QUALIFIED_SUPPLIER(9, "QUALIFIED_SUPPLIER"),
    SOURCING_NO_QUALIFIED_SUPPLIER(10, "NO_QUALIFIED_SUPPLIER"),
    SOURCING_DELETED(11, "DELETED"),
    SOURCING_REJECTED(12,  "REJECTED"),
    SOURCING_NO_SUPPLIER_RESPONSE(13,  "NO_SUPPLIER_RESPONSE"),
    SOURCING_NO_SUPPLIER_SELECTED(14,  "NO_SUPPLIER_SELECTED"),;

    private final Integer id;
    private final String code;

    SourcingStatus(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public Integer id() {
        return this.id;
    }
    public String code() {
        return this.code;
    }
}
