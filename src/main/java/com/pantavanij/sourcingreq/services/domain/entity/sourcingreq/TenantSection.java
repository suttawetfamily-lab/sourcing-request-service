package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantSection")
public class TenantSection {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId", nullable = false)
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TemplateId", nullable = false)
    @JsonBackReference
    private TenantTemplate template;
    
    @Column(name = "PrivilegeCode")
    private String privilegeCode;

    @Column(name = "Type")
    private String type;

    @Column(name = "SectionName")
    private String sectionName;
    
    @Column(name = "SectionTitle")
    private String sectionTitle;

    @Column(name = "SectionSubTitle")
    private String sectionSubTitle;
    
    @Column(name = "Step")
    private Integer step;
    
    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "Visible")
    private boolean visible;

    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    @OneToMany(mappedBy = "tenantSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<TenantSectionDetail> fields;

    @OneToMany(mappedBy = "tenantSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<TenantSectionDetail> tenantSectionDetailList;

}
