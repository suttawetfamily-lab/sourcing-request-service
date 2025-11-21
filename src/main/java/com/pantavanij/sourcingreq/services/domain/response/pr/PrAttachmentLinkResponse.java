package com.pantavanij.sourcingreq.services.domain.response.pr;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PrAttachmentLinkResponse {

    @JsonProperty("rel")
    private String rel;

    @JsonProperty("href")
    private String href;

    @JsonProperty("name")
    private String name;

    @JsonProperty("kind")
    private String kind;
}
