package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "RequestItemReport")
public class RequestItemReport {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TemplateId")
    @JsonBackReference
    private TenantTemplate tenantTemplate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MenuPrivilegeId")
    @JsonBackReference
    private MenuPrivilege menuPrivilege;

    @Column(name = "Code")
    private String code;

    @Column(name = "Name")
    private String name;

    @Column(name = "PrivilegeCode")
    private String privilegeCode;

    @Column(name = "ExportFileName")
    private String exportFileName;

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

    @OneToMany(mappedBy = "requestItemReport", cascade = CascadeType.ALL)
    @ToString.Exclude
    //@JsonIgnoreProperties
    //@JsonIgnoreProperties({"requestItemReport"})
    @JsonIgnore
    private List<TenantRequestItemReport> tenantRequestItemReportList;

}