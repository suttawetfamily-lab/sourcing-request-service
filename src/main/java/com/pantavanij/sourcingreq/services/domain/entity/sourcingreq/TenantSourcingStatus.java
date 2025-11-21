package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantSourcingStatusKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "TenantSourcingStatus")
public class TenantSourcingStatus {

    @EmbeddedId
    private TenantSourcingStatusKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantId")
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("sourcingStatusId")
    @JoinColumn(name = "SourcingStatusId")
    @JsonBackReference
    private SourcingStatus sourcingStatus;
    
    @Column(name = "PurchaserOwner")
    private String purchaserOwner;

    @Column(name = "PurchaserNotOwner")
    private String purchaserNotOwner;

    @Column(name = "Requester")
    private String requester;

    @Column(name = "Reviewer")
    private String reviewer;

    @Column(name = "Approver")
    private String approver;

    @Column(name = "Remark")
    private String remark;

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
