package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Request")
public class Request {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @Column(name = "RequestNo")
    private String requestNo;

    @Column(name = "WorkflowInstanceId")
    private Long workflowInstanceId;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "RequestTypeId")
    private Integer requestTypeId;

    @Column(name = "RequestName")
    private String requestName;

    @Column(name = "Objective")
    private String objective;

    @Column(name = "ExpectedDate")
    private Timestamp expectedDate;

    @Column(name = "BudgetRefNo")
    private String budgetRefNo;

    @Column(name = "ProjectCode")
    private String projectCode;
//
    @Column(name = "ProjectName")
    private String projectName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CurrencyId")
    @JsonBackReference
    private Currency currency;

    @Column(name = "Budget")
    private BigDecimal budget;

    @Column(name = "RequestDate")
    private Timestamp requestDate;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Mobile")
    private String mobile;

    @Column(name = "Email")
    private String email;

    @Column(name = "StatusId")
    private Integer statusId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(updatable=false,insertable=false, name="TenantId", referencedColumnName="TenantId"),
            @JoinColumn(updatable=false,insertable=false, name="StatusId", referencedColumnName="RequestStatusId")
    })
    @JsonBackReference
    private TenantRequestStatus requestStatus;

    @Column(updatable=false,insertable=false, name = "DeptApprovalStatusId")
    private Integer deptApprovalStatusId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DeptApprovalStatusId")
    @JsonBackReference
    private DeptApprovalStatus deptApprovalStatus;

    @Column(name = "ApprovalStatusId")
    private Integer approvalStatusId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(updatable=false,insertable=false, name="TenantId", referencedColumnName="TenantId"),
            @JoinColumn(updatable=false,insertable=false, name="ApprovalStatusId", referencedColumnName="ApprovalStatusId")
    })
    @JsonBackReference
    private TenantApprovalStatus approvalStatus;

    @Column(name = "NoteToApprovers")
    private String noteToApprovers;

    @Column(name = "ApprovalType")
    private String approvalType;

    @Column(name = "ApprovalDate")
    private Timestamp approvalDate;

    @Column(name = "ApprovedReason")
    private String approvedReason;

    @Column(name = "RejectedReason")
    private String rejectedReason;

    @Column(name = "CancellationReason")
    private String cancellationReason;

    @Column(name = "AssignedBy")
    private String assignedBy;

    @Column(name = "VatType")
    private String vatType;

    @Column(name = "DelegateActionBy")
    private String delegateActionBy;

    @Column(name = "DelegateActionDate")
    private Timestamp delegateActionDate;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    @Column(name = "IsCopyToPRFailed")
    private Boolean isCopyToPRFail;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<RequestItem> requestItemList;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<RequestAttachment> requestAttachmentList;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<RequestPurchaser> requestPurchaserList;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<RequestApprover> requestDeptApproverList;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<RequestProject> requestProjectList;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<RequestDepartment> requestDepartmentList;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<RequestReportLine> requestReportLineList;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<RequestReviewer> requestReviewerList;
}
