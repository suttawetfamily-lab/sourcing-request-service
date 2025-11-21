package com.pantavanij.sourcingreq.services.enums;

public enum AttachmentFileGroup {
    REQ("Request"),
    REQI("Request Item"),
    REQIEX("Existing Price Item"),
    REQIEC("Exceptional Sourcing Item"),
    REQIERFX("eRFX");

    private final String description;

    AttachmentFileGroup(String description) {
        this.description = description;
    }


    public String description() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.name().concat(": ").concat(this.description);
    }

    public static String description(String name) {
        for (AttachmentFileGroup statusCodeEnum : AttachmentFileGroup.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}
