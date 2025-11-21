package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class RequestAttachmentDto {
    private AttachmentDto attachment;
    private Integer lineNum;
    private boolean sendtoSupplier;
    private String note;
}
