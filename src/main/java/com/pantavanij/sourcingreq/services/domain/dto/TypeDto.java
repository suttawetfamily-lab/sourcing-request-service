package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;
import java.sql.*;

@Data
public class TypeDto {
    private OptionDto typeObj;
    private Integer recId;
    private Integer sequence;
    private String code;
    private String name;
    private boolean active;
    private boolean isDefault;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
