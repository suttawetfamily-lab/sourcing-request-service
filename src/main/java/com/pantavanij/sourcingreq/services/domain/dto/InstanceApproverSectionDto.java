package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class InstanceApproverSectionDto {
    private String sectionName;
    private String sectionLabel;
    private Integer numberOfApproverRequired;
    private String status;
    List<InstanceApproverDto> approvers;
    private boolean canAddNewItem;
    private boolean hiddenStatus;
    private boolean hiddenComment;
    private String endpointURL;
}
