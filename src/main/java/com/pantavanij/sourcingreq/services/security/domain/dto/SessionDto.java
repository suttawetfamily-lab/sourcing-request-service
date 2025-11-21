package com.pantavanij.sourcingreq.services.security.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionDto {

    private String sid;
    private long eboId;
    private String sysUserId;
    private String userName;
    private String borgName;

}
