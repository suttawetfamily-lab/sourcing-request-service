package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateFormIdRequest {
    @NotNull
    private Long formId;
    private Boolean checkPermission;
}
