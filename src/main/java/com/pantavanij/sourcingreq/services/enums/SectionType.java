package com.pantavanij.sourcingreq.services.enums;

public enum SectionType {
    SECTION_TYPE_REQ("REQ"),
    SECTION_TYPE_REQI("REQI"),
    SECTION_TYPE_REQIEX("REQIEX"),
    SECTION_TYPE_REQIEC("REQIEC"),
    SECTION_TYPE_RPT("RPT");

    private final String code;

    SectionType(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }
}
