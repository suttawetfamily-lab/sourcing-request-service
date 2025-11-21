package com.pantavanij.sourcingreq.services.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum WorkflowTemplateStatus {
    ACTIVE,
    INACTIVE;

    public String getEnumNames() {
        List<String> names = Arrays.stream(WorkflowTemplateStatus.values())
                .map(Enum::name).collect(Collectors.toList());
        return "Enum: " + names.toString();
    }
}
