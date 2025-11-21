package com.pantavanij.sourcingreq.services.enums;

public enum ValidatorType {
    VALIDATOR_STRING(1),
    VALIDATOR_STRICT(2),
    VALIDATOR_TRIM(3),
    VALIDATOR_MIN(4),
    VALIDATOR_MAX(5),
    VALIDATOR_REQUIRED(6),
    VALIDATOR_OBJECT(7),
    VALIDATOR_ARRAY(8),
    VALIDATOR_ARRAY_FILE(9);

    private final Integer id;

    ValidatorType(Integer id) {
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }
}
