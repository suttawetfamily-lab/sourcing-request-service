package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "SupplierMailingLog")
public class SupplierMailingLog {
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Id
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SupplierMailingQueueId")
    @JsonBackReference
    private SupplierMailingQueue supplierMailingQueue;

    @Column(name = "SupplierCategoryValueLocal")
    private String supplierCategoryValueLocal;

    @Column(name = "SupplierCategoryValueInter")
    private String supplierCategoryValueInter;

    @Column(name = "SupplierNameLocal")
    private String supplierNameLocal;
    
    @Column(name = "SupplierNameInter")
    private String supplierNameInter;
    
    @Column(name = "ContactNameLocal")
    private String contactNameLocal;
    
    @Column(name = "ContactNameInter")
    private String contactNameInter;
    
    @Column(name = "Email")
    private String email;
    
    @Column(name = "SentDate")
    private Timestamp sentDate;

    @Column(name = "Message")
    private String message;

    @Column(name = "Status")
    private String status;
    
    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

}
