package com.pantavanij.sourcingreq.services.domain.request.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.pr.AttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.pr.DFFTicketNumberDto;
import com.pantavanij.sourcingreq.services.domain.dto.pr.LineDto;
import lombok.Data;

import java.util.List;

@Data
public class CreateOraclePrRequest {

    @JsonProperty("PreparerEmail")
    private String preparerEmail;

    @JsonProperty("ExternallyManagedFlag")
    private boolean externallyManagedFlag;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("InterfaceSourceCode")
    private String interfaceSourceCode;

    @JsonProperty("RequisitioningBU")
    private String requisitioningBU;

    @JsonProperty("TaxationCountryCode")
    private String taxationCountryCode;

    @JsonProperty("TaxationCountry")
    private String taxationCountry;

    @JsonProperty("DFF")
    private List<DFFTicketNumberDto> dff;

    @JsonProperty("lines")
    private List<LineDto> lines;
}
