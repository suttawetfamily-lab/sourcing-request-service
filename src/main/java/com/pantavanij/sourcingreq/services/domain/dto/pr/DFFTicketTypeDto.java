package com.pantavanij.sourcingreq.services.domain.dto.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DFFTicketTypeDto {

    @JsonProperty("ptvnTicketType")
    private String ptvnTicketType;
}
