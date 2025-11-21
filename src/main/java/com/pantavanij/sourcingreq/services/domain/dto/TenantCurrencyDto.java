package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.sql.*;
import java.util.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantCurrencyDto {
    private CurrencyDto currencyObj;
    private boolean isDefault;
    private boolean active;
    private Integer sequence;
    private Timestamp createdDate;
    private String createdBy;
    private Timestamp updatedDate;
    private String updatedBy;
}
