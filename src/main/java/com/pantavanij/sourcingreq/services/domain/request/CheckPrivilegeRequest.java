package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CheckPrivilegeRequest {
    @NotNull
    private Long sysUserId;
    private Long borgId;
    @NotEmpty
    private List<String> privileges;
}
