package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "RequestApprover")
public class RequestApprover {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @Column(insertable = false, updatable = false, name = "ApproverId")
    private Integer approverId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ApproverId")
    @JsonBackReference
    private Approver approver;

    @Column(insertable = false, updatable = false, name = "DeptApprovalStatusId")
    private Integer deptApprovalStatusId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DeptApprovalStatusId")
    @JsonBackReference
    private DeptApprovalStatus deptApprovalStatus;
    
    @Column(name = "Sequence")
    private Integer sequence;
    
    @Column(name = "Comment")
    private String comment;
    
    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

}
