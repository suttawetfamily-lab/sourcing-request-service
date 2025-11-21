package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantMasterGridField")
@IdClass(com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantMasterGridFieldPK.class)
public class TenantMasterGridField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MasterGridFieldId")
    @JsonBackReference
    private MasterGridField masterGridField;
    
    @Column(name = "Code")
    private String code;
    
    @Column(name = "DisplayName")
    private String displayName;

    
    @Column(name = "GroupName")
    private String groupName;

    
    @Column(name = "Sequence")
    private int sequence;

    
    @Column(name = "Searchable")
    private boolean searchable;

    
    @Column(name = "Sorting")
    private String sorting;

    
    @Column(name = "Width")
    private Integer width;

    
    @Column(name = "Type")
    private String type;

    
    @Column(name = "visible")
    private boolean visible;

    
    @Column(name = "CreatedBy")
    private String createdBy;

    
    @Column(name = "CreatedDate")
    private Date createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Date updatedDate;

}
