package com.pantavanij.sourcingreq.services.domain.dto.supplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OracleContactDto {

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("AdministrativeContactFlag")
    private Boolean administrativeContactFlag;

    @JsonProperty("Email")
    private String email;
}
