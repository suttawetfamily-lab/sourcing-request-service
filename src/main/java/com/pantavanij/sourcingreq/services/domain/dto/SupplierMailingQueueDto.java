package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.domain.dto.SupplierMailingLogDto;
import lombok.*;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupplierMailingQueueDto {
    private Long recId;
    private String tenant;
    private String requestName;
    private Timestamp submitDate;
    private String category;
    private String subCategory;
    private Integer numberOfContact;
    private String status;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<SupplierMailingLogDto> supplierMailingLogList;
}
