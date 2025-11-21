package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetProfitDto {

    private String stYear;
    private String stValue;
    private String ndYear;
    private String ndValue;
    private String rdYear;
    private String rdValue;
}
