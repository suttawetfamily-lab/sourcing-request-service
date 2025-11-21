package com.pantavanij.sourcingreq.services.domain.dto.category;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class CategorySearchDataDto {
    private Integer recId;
    private String code;
    private String name;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

    private String createdByName;
    private String updatedByName;
}
