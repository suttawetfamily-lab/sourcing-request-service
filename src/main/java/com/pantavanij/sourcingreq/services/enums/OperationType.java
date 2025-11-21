package com.pantavanij.sourcingreq.services.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OperationType {
    IS,
    ISNOT,
    GREATERTHAN,
    LESSTHAN,
    STARTWITH,
    ENDWITH,
    NOTSTARTWITH,
    NOTENDWITH,
    CONTAIN,
    IN,
    NOTIN;

    public static String getEnumNames() {
        List<String> names = Arrays.stream(OperationType.values())
                .map(Enum::name).collect(Collectors.toList());
        return "Enum: " + names.toString();
    }
}
