package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;
import java.sql.*;

@Data
public class ProjectChangeLogHeaderDto {
    private int recId;
    private String fileId;
    private String fileName;
    private int fileSize;
    private String createdBy;
    private Timestamp createdDate;
}
