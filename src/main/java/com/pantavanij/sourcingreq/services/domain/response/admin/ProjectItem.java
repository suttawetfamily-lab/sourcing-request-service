package com.pantavanij.sourcingreq.services.domain.response.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectItem {
    private Long projectId;
    private String projectCode;
    private String projectName;
    private String updateBy;
    private Timestamp updateDate;
}
