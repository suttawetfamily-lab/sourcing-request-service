package com.pantavanij.sourcingreq.services.domain.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private Boolean isError;
    private String errorMsg;

}
