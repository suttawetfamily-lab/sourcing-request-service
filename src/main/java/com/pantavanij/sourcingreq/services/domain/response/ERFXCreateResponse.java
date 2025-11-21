package com.pantavanij.sourcingreq.services.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class ERFXCreateResponse {
    String tenantId;
    String docNum;
    List<ERFXItemResponse> items;
    Integer erfxNum;
    String erfxStatus;
}