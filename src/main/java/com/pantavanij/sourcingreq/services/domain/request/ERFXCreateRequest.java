package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.ERFXAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.ERFXItemDto;
import lombok.Data;

import java.util.List;

@Data
public class ERFXCreateRequest {
    private Integer requestTemplate;
    private String docNum;
    private String erfxName;
    private String description;
    private String currency;
    private String department;
    private boolean includeVat;
    private List<ERFXItemDto> items;
    private List<ERFXAttachmentDto> attachments;
    private String appName;
    private String budget;
    private String requesterName;
    private String phone;
    private String mobile;
    private String email;
    private String targetDate;
    private String reminder;
    private String organization;
}