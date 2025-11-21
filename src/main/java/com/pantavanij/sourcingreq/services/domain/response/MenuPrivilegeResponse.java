package com.pantavanij.sourcingreq.services.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuPrivilegeResponse {
    private String pathUrl;
    private boolean isPermission;
}
