package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import java.sql.*;

@Data
public class ProjectChangeLogDetailDto {
    private int recId;
    private int changeLogHeaderId;
    private String code;
    private String name;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
