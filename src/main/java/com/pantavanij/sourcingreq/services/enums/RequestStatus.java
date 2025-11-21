package com.pantavanij.sourcingreq.services.enums;

public enum RequestStatus {
    REQUEST_DRAFT(1),
    REQUEST_AWAITING(2),
    REQUEST_PARTIAL_COMPLETED(3),
    REQUEST_COMPLETED(4),
    REQUEST_REJECTED(5),
    REQUEST_CANCELLED(6),
    REQUEST_PENDING(7),

    ;

    private final Integer id;

    RequestStatus(Integer id) {
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }
}
