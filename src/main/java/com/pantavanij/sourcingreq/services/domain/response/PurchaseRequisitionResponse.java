package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.HeaderPRDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseRequisitionResponse {
    @JsonProperty("Header")
    private HeaderPRDto header;
    @JsonProperty("Data")
    private Object data;
}
