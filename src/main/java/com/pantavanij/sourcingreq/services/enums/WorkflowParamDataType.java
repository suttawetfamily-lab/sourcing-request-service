package com.pantavanij.sourcingreq.services.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum WorkflowParamDataType {
    INTEGER,
    DECIMAL,
    FREETEXT,
    DATE,
    BOOLEAN;

    public String getEnumNames() {
        List<String> names = Arrays.stream(WorkflowParamDataType.values())
                .map(Enum::name).collect(Collectors.toList());
        return "Enum: " + names.toString();
    }
}
