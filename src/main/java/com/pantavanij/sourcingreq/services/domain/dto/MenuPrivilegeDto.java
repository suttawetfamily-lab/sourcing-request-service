package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuPrivilegeDto {
    private Integer recId;
    private String pathUrl;
    private Integer typeId;
    private String privilegeCode;
    private String label;
    private String menuName;
    private RequestItemReportDto requestItemReport;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
