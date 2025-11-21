package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HeaderPRDto {
    @JsonProperty("Code")
    private Integer code;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("CountDoc")
    private Integer countDoc;
    @JsonProperty("CountDocToReview")
    private Integer countDocToReview;
    @JsonProperty("Key1")
    private String key1;
    @JsonProperty("Key2")
    private String key2;
    @JsonProperty("Key3")
    private String key3;
    @JsonProperty("PRNumber")
    private String prNumber;
}