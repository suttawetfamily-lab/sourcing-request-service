package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class MailingConfigResponse {
    private boolean srNotifySupplier;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String scCatLevel1;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String scCatLevel2;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String scCatLevel3;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String scCatLevel4;
}