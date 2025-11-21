package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class EPAuthUserDTO {
    Integer sysUserId;
    String loginId;
    String fullName;
    String email;
    String mobilePhone;
    String phone;
    String timezone;
    String department;
}
