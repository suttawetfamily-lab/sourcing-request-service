package com.pantavanij.sourcingreq.services.domain.dto.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DFFTicketNumberDto {

    @JsonProperty("ptvnTicketNumber")
    private String ptvnTicketNumber;
}
