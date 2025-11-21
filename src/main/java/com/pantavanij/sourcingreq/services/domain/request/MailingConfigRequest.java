package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class MailingConfigRequest {
    private String tenant;
    private String opCat;
    private String opSubCat;
}
