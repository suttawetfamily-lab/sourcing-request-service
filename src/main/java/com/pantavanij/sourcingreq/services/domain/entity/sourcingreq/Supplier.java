package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Supplier")
public class Supplier {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @Column(name = "ShortName")
    private String shortName;

    @Column(name = "FullCompanyNameLocal")
    private String fullCompanyNameLocal;

    @Column(name = "FullCompanyNameEN")
    private String fullCompanyNameEN;

    @Column(name = "CompanyNameLocal")
    private String companyNameLocal;

    @Column(name = "CompanyNameEN")
    private String companyNameEN;

    @Column(name = "BranchNameLocal")
    private String branchNameLocal;

    @Column(name = "BranchNameEN")
    private String branchNameEN;

    @Column(name = "TaxId")
    private String taxId;

    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "[Default]")
    private boolean isDefault;

    @Column(name = "[Active]")
    private boolean active;

    @Column(name = "[ActiveOnERP]")
    private boolean activeOnERP;
    
    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;
}
