package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasterGridFieldDto {
    private String code;
    private String displayName;
    private Integer sequence;
    private String sorting;
    private Integer width;
    private String type;
}
