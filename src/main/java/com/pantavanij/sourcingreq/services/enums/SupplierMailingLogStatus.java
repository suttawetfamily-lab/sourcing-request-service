package com.pantavanij.sourcingreq.services.enums;

public enum SupplierMailingLogStatus {
    SUPPLIER_MAILING_LOG_AWAITING(1,  "Awaiting"),
    SUPPLIER_MAILING_LOG_SENT(2, "Sent"),
    SUPPLIER_MAILING_LOG_FAILED(3, "Failed");

    private final Integer id;
    private final String code;

    SupplierMailingLogStatus(Integer id, String code) {
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
