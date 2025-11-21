package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class RequestItemLocationDto {
    private RequestLocationDto location;
    private String deliveryLocation;
    private String contactName;
    private String phone;
}
