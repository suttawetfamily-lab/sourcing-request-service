package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestReportKey;
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
@Table(name = "TenantRequestReport")
public class TenantRequestReport {
    @EmbeddedId
    private TenantRequestReportKey id;

    @MapsId("tenantId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @MapsId("requestReportId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestReportId")
    @JsonBackReference
    private RequestReport requestReport;

    @MapsId("tenantSectionDetailId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantSectionDetailId")
    @JsonBackReference
    private TenantSectionDetail tenantSectionDetail;

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