package com.pantavanij.sourcingreq.services.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum WorkflowStageType {
    PARALLEL, SEQUENTIAL;

    public String getEnumNames() {
        List<String> names = Arrays.stream(WorkflowStageType.values())
                .map(Enum::name).collect(Collectors.toList());
        return "Enum: " + names.toString();
    }

    public static WorkflowStageType getValue(String name) {
        for (WorkflowStageType stageType : WorkflowStageType.values()) {
            if (stageType.name().equalsIgnoreCase(name)) {
                return stageType;
            }
        }
        return null;
    }
}
