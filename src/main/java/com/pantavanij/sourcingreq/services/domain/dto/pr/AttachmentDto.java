package com.pantavanij.sourcingreq.services.domain.dto.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AttachmentDto {

    @JsonProperty("DatatypeCode")
    private String datatypeCode;

    @JsonProperty("FileName")
    private String fileName;

    @JsonProperty("FileContents")
    private String fileContents; // base64

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Description")
    private String description;
}
