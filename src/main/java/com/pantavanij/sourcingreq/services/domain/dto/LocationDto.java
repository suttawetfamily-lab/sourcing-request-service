package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationDto {
    private String value;
    private String label;
    private String name;
    private String address;
    private String contactName;
    private String phone;
    private boolean isDefault;
}
