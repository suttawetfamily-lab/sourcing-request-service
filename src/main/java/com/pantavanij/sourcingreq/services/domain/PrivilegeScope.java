package com.pantavanij.sourcingreq.services.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PrivilegeScope implements Serializable {
    private static final long serialVersionUID = 1L;

    String privilegeCode;
    String scope;


}
