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
@Table(name = "ExcSourcingStatus")
public class ExcSourcingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecId")
    private Integer recId;

    @Column(name = "Code", length = 50)
    private String code;

    @Column(name = "Description", length = 200)
    private String description;

    @Column(name = "CanEdit")
    private boolean canEdit;

    @Column(name = "CanDelete")
    private boolean canDelete;

    @Column(name = "CanCancel")
    private boolean canCancel;

    @Column(name = "CanCopyToPR")
    private boolean canCopyToPR;

    @Column(name = "CanViewHistory")
    private boolean canViewHistory;

    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "DisplaySequence")
    private Integer displaySequence;
}
