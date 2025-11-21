package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class InstanceApproverHeaderDto {
    private String headerName;
    List<InstanceApproverSectionDto> approverSections;
}
