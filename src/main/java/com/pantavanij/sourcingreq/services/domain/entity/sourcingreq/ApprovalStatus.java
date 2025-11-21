package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

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
@Table(name = "ApprovalStatus")
public class ApprovalStatus {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;
    
    @Column(name = "Name")
    private String name;
    
    @Column(name = "Description")
    private String description;

    @Column(name = "CanApprove")
    private boolean canApprove;
    
    @Column(name = "CanEdit")
    private boolean canEdit;
    
    @Column(name = "CanDelete")
    private boolean canDelete;
    
    @Column(name = "CanDuplicate")
    private boolean canDuplicate;
    
    @Column(name = "CanCancel")
    private boolean canCancel;
    
    @Column(name = "CanCopyToPR")
    private boolean canCopyToPR;
    
    @Column(name = "CanViewHistory")
    private boolean canViewHistory;
    
    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;

}
