package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.DelegationStatusDto;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class DelegationValidateRequest {
    private Timestamp startDate;
    private Timestamp endDate;
}
