package com.pantavanij.sourcingreq.services.enums;

public enum ExcSourcingStatus {
    EXC_SOURCING_DRAFT(1),
    EXC_SOURCING_PENDING(2),
    EXC_SOURCING_AWAITING_RESPONSE(3),
    EXC_SOURCING_PARTIAL_COMPLETED(4),
    EXC_SOURCING_COMPLETED(5),
    EXC_SOURCING_REJECTED(12),
    EXC_SOURCING_CANCELLED(7),
    EXC_SOURCING_QUALIFIED_SUPPLIER(9),
    EXC_SOURCING_NO_QUALIFIED_SUPPLIER(10);


    ;

    private final Integer id;

    ExcSourcingStatus(Integer id) {
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }
}
