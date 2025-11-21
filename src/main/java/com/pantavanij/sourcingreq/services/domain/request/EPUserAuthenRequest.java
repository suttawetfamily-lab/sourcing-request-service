package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EPUserAuthenRequest extends UserAuthenRequest {

    String password;
    String eid;

    public EPUserAuthenRequest(String username, String password, String eid){
        super(username);
        this.password = password;
        this.eid = eid;
    }
}
