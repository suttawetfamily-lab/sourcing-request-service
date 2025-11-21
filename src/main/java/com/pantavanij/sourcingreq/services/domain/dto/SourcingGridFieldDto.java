package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourcingGridFieldDto {
    private Integer recId;
    private String code;
    private String displayName;
    private Integer sequence;
    private String sorting;
    private Integer width;
    private String type;
    private String privilegeCode;
    private boolean visible;
    private boolean searchable;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

    private String createdByName;
    private String updatedByName;

}
