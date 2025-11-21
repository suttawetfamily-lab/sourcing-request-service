package com.pantavanij.sourcingreq.services.enums;

public enum AttachmentFlag {
    DDNO(1, "DDNO");

    private final Integer id;
    private final String code;

    AttachmentFlag(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public Integer id() { return this.id;}
    public String code() { return this.code;}
}
