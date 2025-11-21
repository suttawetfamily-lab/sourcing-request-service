package com.pantavanij.sourcingreq.services.domain.response.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PrDffResponse {

    @JsonProperty("RequisitionHeaderId")
    private Long requisitionHeaderId;

    @JsonProperty("ptvnTicketNumber")
    private String ptvnTicketNumber;
}
