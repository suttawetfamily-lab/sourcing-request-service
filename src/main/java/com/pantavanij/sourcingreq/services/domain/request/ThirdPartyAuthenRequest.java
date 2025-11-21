package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThirdPartyAuthenRequest extends UserAuthenRequest{

    String tenantId;

    public ThirdPartyAuthenRequest(String username, String tenantId){
        super(username);
        this.tenantId = tenantId;
    }
}
