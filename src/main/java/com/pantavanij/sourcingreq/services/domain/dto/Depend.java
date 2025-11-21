package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class Depend {
    private String name;
    private List<String> values;
}
