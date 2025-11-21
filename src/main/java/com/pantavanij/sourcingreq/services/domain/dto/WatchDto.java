package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

@Data
@Builder
public class WatchDto {
    private String fieldName;
    private NameObjectDto<?> nameObj;
    private String originalFieldName;
    private String updatedFieldName;
    private String values;
    private String groupName;
}
