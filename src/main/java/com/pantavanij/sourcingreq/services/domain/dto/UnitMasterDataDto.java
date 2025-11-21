package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import java.sql.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitMasterDataDto {
    private Integer recId;
    private String code;
    private String name;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
