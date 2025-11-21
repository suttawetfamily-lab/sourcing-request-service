package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.sql.*;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailActivityDto {
    private Long recId;
    private String activityName;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
