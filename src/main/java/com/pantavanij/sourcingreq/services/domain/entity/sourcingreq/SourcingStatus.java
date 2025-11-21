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
@Table(name = "SourcingStatus")
public class SourcingStatus {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;

    @Column(name = "Code")
    private String code;
    
    @Column(name = "Description")
    private String description;

    @Column(name = "CanEdit")
    private boolean canEdit;

    @Column(name = "CanReject")
    private boolean canReject;

    @Column(name = "CanDelete")
    private boolean canDelete;

    @Column(name = "ServiceName")
    private String serviceName;

    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "DisplaySequence")
    private Integer displaySequence;

}
