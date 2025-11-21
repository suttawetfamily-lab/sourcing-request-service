package com.pantavanij.sourcingreq.services.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailResponse {
    private boolean isError;
    private String message;
    private String email;
}
