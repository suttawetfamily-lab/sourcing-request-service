package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Category")
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;
    
    @Column(name = "Code")
    private String code;
    
    @Column(name = "Name")
    private String name;

    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "[Default]")
    private boolean isDefault;

    @Column(name = "[Active]")
    private boolean active;
    
    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<CategoryPurchaser> purchaserList;

    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<SubCategory> subCategoryList;
}
