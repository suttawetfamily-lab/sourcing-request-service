package com.pantavanij.sourcingreq.services.enums;

public enum ThirdPartyRole {
    THIRD_PARTY_ROLE_DATA_PROCESSOR(1,  "Data Processor"),
    THIRD_PARTY_ROLE_DATA_CONTROLLER(2, "Data Controller");

    private final Integer id;
    private final String code;

    ThirdPartyRole(Integer id, String code) {
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
