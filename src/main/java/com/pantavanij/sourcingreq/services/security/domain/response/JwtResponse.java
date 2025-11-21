package com.pantavanij.sourcingreq.services.security.domain.response;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = 1250166508152483573L;

    private final String token;

    public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
