package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.sql.*;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubCategoryDto {
    private Integer recId;
    private Integer categoryId;
    private String code;
    private String name;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<PurchaserDto> purchaserList;
}
