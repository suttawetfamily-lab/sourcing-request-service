package com.pantavanij.sourcingreq.services.enums;

public enum SupplierMailingQueueStatus {
    SUPPLIER_MAILING_QUEUE_AWAITING(1,  "Awaiting"),
    SUPPLIER_MAILING_QUEUE_COMPLETED(2, "Completed"),
    SUPPLIER_MAILING_QUEUE_INCOMPLETE(3, "Incomplete");

    private final Integer id;
    private final String code;

    SupplierMailingQueueStatus(Integer id, String code) {
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
