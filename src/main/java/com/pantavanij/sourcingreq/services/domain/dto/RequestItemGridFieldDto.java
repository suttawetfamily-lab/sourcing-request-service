package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestItemGridFieldDto {
    private Integer recId;
    private String code;
    private String displayName;
    private Integer sequence;
    private Integer width;
    private String type;
    private String align;
    private boolean visible;
    private String tooltip;
    private String childField;

    private String privilegeCode;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private String createdByName;
    private String updatedByName;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<RequestItemChildFieldDto> childFields;
}
