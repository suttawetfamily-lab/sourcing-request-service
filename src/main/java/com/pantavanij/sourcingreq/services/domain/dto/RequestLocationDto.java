package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class RequestLocationDto {
    private Integer recId;
    private String name;
    private String address;
    private String contactName;
    private String phone;
    private boolean canEditAddress;
    private boolean isDefault;
}
