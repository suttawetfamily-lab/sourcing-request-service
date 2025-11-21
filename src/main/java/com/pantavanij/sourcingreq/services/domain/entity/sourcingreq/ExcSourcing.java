package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ExcSourcing")
public class ExcSourcing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @Column(name = "ExcSourcingDocNo")
    private String excSourcingDocNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ExcSourcingStatusId")
    @NotFound(action = NotFoundAction.IGNORE)
    private ExcSourcingStatus excSourcingStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "TenantId", referencedColumnName = "TenantId", insertable = false, updatable = false),
            @JoinColumn(name = "ExcSourcingStatusId", referencedColumnName = "ExcSourcingStatusId", insertable = false, updatable = false)
    })
    @NotFound(action = NotFoundAction.IGNORE)
    private TenantExcSourcingStatus tenantExcSourcingStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApprovalStatusId")
    @NotFound(action = NotFoundAction.IGNORE)
    private DeptApprovalStatus approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DeptApprovalStatusId")
    @NotFound(action = NotFoundAction.IGNORE)
    private DeptApprovalStatus deptApprovalStatus;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    // ความสัมพันธ์กลับไปหา ExcSourcingRequestItem
    @OneToMany(mappedBy = "excSourcing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExcSourcingRequestItem> excSourcingRequestItems;

    @OneToMany(mappedBy = "excSourcing", fetch = FetchType.LAZY)
    private List<ExcSourcingApprover> excSourcingApprovers;

    @OneToMany(mappedBy = "excSourcing", fetch = FetchType.LAZY)
    private List<ExcSourcingPurchaser> excSourcingPurchasers;
}
