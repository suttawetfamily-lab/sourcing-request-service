package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TenantExcSourcingStatus")
public class TenantExcSourcingStatus {

    @EmbeddedId
    private TenantExcSourcingStatusKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantId")
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("excSourcingStatusId")
    @JoinColumn(name = "ExcSourcingStatusId")
    @JsonBackReference
    private ExcSourcingStatus excSourcingStatus;

    @Column(name = "Name", length = 100, nullable = false)
    private String name;

    @Column(name = "Description", length = 500, nullable = false)
    private String description;

    @Column(name = "CanEdit", nullable = false)
    private boolean canEdit;

    @Column(name = "CanDelete", nullable = false)
    private boolean canDelete;

    @Column(name = "CanCancel", nullable = false)
    private boolean canCancel;

    @Column(name = "CanReject", nullable = false)
    private boolean canReject;

    @Column(name = "CanCopyToPR", nullable = false)
    private boolean canCopyToPR;

    @Column(name = "CanViewHistory", nullable = false)
    private boolean canViewHistory;

    @Column(name = "CreatedBy", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "CreatedDate", nullable = false)
    private Timestamp createdDate;

    @Column(name = "UpdatedBy", length = 50)
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;
}
