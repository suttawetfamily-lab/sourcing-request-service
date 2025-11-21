package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.supplier.LinkDto;
import com.pantavanij.sourcingreq.services.domain.dto.supplier.SupplierItemDto;
import lombok.Data;
import java.util.List;

@Data
public class SupplierResponse {

    @JsonProperty("items")
    private List<SupplierItemDto> items;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("hasMore")
    private Boolean hasMore;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("links")
    private List<LinkDto> links;
}

