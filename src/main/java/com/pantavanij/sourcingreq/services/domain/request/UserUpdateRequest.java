package com.pantavanij.sourcingreq.services.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserUpdateRequest {

    List<String> username;
    String tenantId;

}
