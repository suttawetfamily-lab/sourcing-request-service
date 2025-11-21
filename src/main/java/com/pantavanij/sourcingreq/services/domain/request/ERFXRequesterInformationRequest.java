package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.ERFXAdditionalDataItemDto;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ERFXRequesterInformationRequest {
    private String requesterName;
    private String department;
    private String phone;
    private String mobile;
    private String email;
    private String targetDate;
    private String reminder;
}