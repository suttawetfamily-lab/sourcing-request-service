package com.pantavanij.sourcingreq.services.security.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class JwtRequest {
    @NotNull
    private String sid;
    @NotNull
    private long sysUserId;
}
