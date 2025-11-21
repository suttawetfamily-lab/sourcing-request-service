package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "RequestReport")
public class RequestReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "PrivilegeCode")
    private String privilegeCode;

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

    @OneToMany(mappedBy = "requestReport", cascade = CascadeType.ALL)
    @ToString.Exclude
    //@JsonIgnoreProperties
    //@JsonIgnoreProperties({"requestItemReport"})
    @JsonIgnore
    private List<TenantRequestReport> tenantRequestReportList;

}