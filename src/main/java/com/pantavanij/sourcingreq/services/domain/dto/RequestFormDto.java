package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestFormDto {
    private int id;
    private String sectionName;
    private String sectionTitle;
    private List<FieldDto> fields;






}

