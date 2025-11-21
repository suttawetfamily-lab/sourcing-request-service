package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import lombok.*;

import java.sql.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantEmailActivityDto {
    private Tenant tenant;
    private EmailActivity emailActivity;
    private Activity activity;
    private boolean purchaser;
    private boolean approver;
    private boolean reportLine;
    private boolean reviewer;
    private boolean requester;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
