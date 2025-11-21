package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import java.sql.*;

@Data
public class CurrencyMasterDto {
    private Integer recId;
    private String code;
    private String name;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
