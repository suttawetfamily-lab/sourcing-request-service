package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestItemReportKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantRequestItemReport")
public class TenantRequestItemReport {
    @EmbeddedId
    private TenantRequestItemReportKey id;

    @MapsId("tenantId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @MapsId("requestItemReportId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestItemReportId")
    @JsonBackReference
    private RequestItemReport requestItemReport;

    @MapsId("tenantSectionDetailId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantSectionDetailId")
    @JsonBackReference
    private TenantSectionDetail tenantSectionDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TemplateId")
    @JsonBackReference
    private TenantTemplate tenantTemplate;

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

}