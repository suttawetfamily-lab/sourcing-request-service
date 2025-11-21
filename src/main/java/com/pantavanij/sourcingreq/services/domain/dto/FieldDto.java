package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.enums.InputType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FieldDto {
    private int id;
    private String name;
    private InputType type;
    private String tooltip;
    private String label;
    private int span;
    private int preSpan;
    private int postSpan;
    private String placeholder;
    private List<String> validations;
    private Map<String, String> params;
    private List<Depend> depends;
    private PreSelect preSelect;
}
