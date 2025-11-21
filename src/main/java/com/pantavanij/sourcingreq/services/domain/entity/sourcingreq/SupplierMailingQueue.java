package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "SupplierMailingQueue")
public class SupplierMailingQueue {
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Id
    @Column(name = "RecId")
    private Long recId;
    
    @Column(name = "Tenant")
    private String tenant;

    @Column(name = "OrgName")
    private String orgName;
    
    @Column(name = "RequestName")
    private String requestName;
    
    @Column(name = "SubmitDate")
    private Timestamp submitDate;
    
    @Column(name = "Category")
    private String category;
    
    @Column(name = "SubCategory")
    private String subCategory;

    @Column(name = "CatLevel1Id")
    private Integer catLevel1Id;

    @Column(name = "CatLevel2Id")
    private Integer catLevel2Id;

    @Column(name = "CatLevel3Id")
    private Integer catLevel3Id;

    @Column(name = "NumberOfContact")
    private Integer numberOfContact;
    
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

    @OneToMany(mappedBy = "supplierMailingQueue", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<SupplierMailingLog> supplierMailingLogList;

}
