package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ApproverDto {
    private Integer recId;
    private Integer userId;
    private String loginId;
    private String approverName;
    private String email;
    private String phone;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
