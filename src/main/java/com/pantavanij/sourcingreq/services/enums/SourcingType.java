package com.pantavanij.sourcingreq.services.enums;

public enum SourcingType {
    SOURCING_TYPE_DRAFT(1),
    SOURCING_TYPE_EXISTING_PRICE(2),
    SOURCING_TYPE_ERFX(3),
    SOURCING_TYPE_EXCEPTIONAL_SOURCING(4),;

    private final Integer id;

    SourcingType(Integer id) {
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }
}
