package com.pantavanij.sourcingreq.services.enums;

public enum RequestType {
    REQUEST_TYPE_QUANTITY(1, "quantity","Standard Template 1: (price unit/quantity)"),
    REQUEST_TYPE_CONDITION(2, "condition","Standard Template 2: (price unit/condition)");

    private final Integer id;
    private final String code;
    private final String description;

    RequestType(Integer id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public Integer id() {
        return this.id;
    }

    public String code() {
        return this.code;
    }

    public String description() {
        return this.description;
    }

    public boolean isQuantity(Integer typeId, String code) {
        return (typeId == RequestType.REQUEST_TYPE_QUANTITY.id && !code.equalsIgnoreCase(RequestType.REQUEST_TYPE_CONDITION.code));
    }

    public boolean isCondition(Integer typeId, String code) {
        return (typeId == RequestType.REQUEST_TYPE_CONDITION.id && !code.equalsIgnoreCase(RequestType.REQUEST_TYPE_QUANTITY.code));
    }
}
