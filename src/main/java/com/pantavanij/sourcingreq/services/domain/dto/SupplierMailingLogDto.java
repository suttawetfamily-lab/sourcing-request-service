package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupplierMailingLogDto {
    private Long recId;
    private SupplierMailingQueueDto supplierMailingQueue;
    private String supplierCategoryValueLocal;
    private String supplierCategoryValueInter;
    private String supplierNameLocal;
    private String supplierNameInter;
    private String contactNameLocal;
    private String contactNameInter;
    private String email;
    private Timestamp sentDate;
    private String message;
    private String status;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
