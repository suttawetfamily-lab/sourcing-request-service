package com.pantavanij.sourcingreq.services.enums;

public enum ERFXRedirectPath {
    DRAFT("ep_proceed_to_erfx_draft"),
    CREATE_NEW("ep_to_erfx_nsr"),
    CHECK_STATUS("ep_to_erfx_csrx"),
    APPROVE_SHORTLIST("ep_to_erfx_ass");

    private final String path;

    ERFXRedirectPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
