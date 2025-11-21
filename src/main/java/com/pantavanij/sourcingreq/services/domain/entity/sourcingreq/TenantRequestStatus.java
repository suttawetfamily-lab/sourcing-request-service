package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.CategoryPurchaserKey;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestStatusKey;
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
@Table(name = "TenantRequestStatus")
public class TenantRequestStatus {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer recId;

    @EmbeddedId
    private TenantRequestStatusKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantId")
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestStatusId")
    @JoinColumn(name = "RequestStatusId")
    @JsonBackReference
    private RequestStatus requestStatus;
    
    private String name;
    
    private String description;

    private boolean canEdit;

    private boolean canDelete;

    private boolean canDuplicate;

    private boolean canCancel;

    private boolean canCopyToPR;

    private boolean canViewHistory;

    private String createdBy;
    
    private Timestamp createdDate;

    private String updatedBy;

    private Timestamp updatedDate;

}
