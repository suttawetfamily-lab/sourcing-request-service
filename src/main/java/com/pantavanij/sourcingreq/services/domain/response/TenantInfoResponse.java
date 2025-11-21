package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantInfoResponse {

    String tenantId;
    String tenantName;
    String idp;
    String mpid;
    Integer eid;
    String invitation_code;
}