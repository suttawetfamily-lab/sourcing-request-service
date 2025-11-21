package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "SubCategory")
public class SubCategory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CategoryId")
    @JsonBackReference
    private Category category;
    
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

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<SubCategoryPurchaser> purchaserList;

}
