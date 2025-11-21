package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SourcingStatusDto {
    private Integer recId;
    private String code;
    private String description;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canReject;
    private boolean canViewHistory;
    private String redirectURL;
    private String createdBy;
    private Timestamp createdDate;
    private boolean showModalMsgInfo;
    private boolean isRemovedLastItem;
}
