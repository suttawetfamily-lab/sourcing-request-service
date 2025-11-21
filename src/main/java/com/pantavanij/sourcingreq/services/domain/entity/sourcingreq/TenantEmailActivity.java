package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import lombok.*;

import javax.persistence.*;
import java.sql.*;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@Table(name = "TenantEmailActivity")
public class TenantEmailActivity {
    @EmbeddedId
    private TenantEmailActivityKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantId")
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("emailActivityId")
    @JoinColumn(name = "EmailActivityId")
    @JsonBackReference
    private EmailActivity emailActivity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("activityId")
    @JoinColumn(name = "activityId")
    @JsonBackReference
    private Activity activity;

    @Column(name = "Purchaser")
    private boolean purchaser;

    @Column(name = "Approver")
    private boolean approver;

    @Column(name = "ReportLine")
    private boolean reportLine;

    @Column(name = "Reviewer")
    private boolean reviewer;

    @Column(name = "Requester")
    private boolean requester;

    @Column(name = "ExcSourcingApprover")
    private boolean excSourcingApprover;

    @Column(name = "ExcSourcingPurchaser")
    private boolean excSourcingPurchaser;

    @Column(name = "[Sequence]")
    private Integer sequence;

    @Column(name = "[Default]")
    private boolean isDefault;

    @Column(name = "Active")
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
