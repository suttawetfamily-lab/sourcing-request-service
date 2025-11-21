package com.pantavanij.sourcingreq.services.enums;

public enum DelegationStatusEnum {
    DELEGATION_UPCOMING(1L),
    DELEGATION_ACTIVE(2L),
    DELEGATION_EXPIRED(3L),
    DELEGATION_CANCELLED(4L);

    private final Long id;

    DelegationStatusEnum(Long id) {
        this.id = id;
    }

    public Long id() {
        return this.id;
    }
}
