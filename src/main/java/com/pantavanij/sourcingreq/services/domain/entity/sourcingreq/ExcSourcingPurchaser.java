package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ExcSourcingPurchaser")
public class ExcSourcingPurchaser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecId", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ExcSourcingId")
    @JsonBackReference
    private ExcSourcing excSourcing;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ApproverId", nullable = false)
    @JsonBackReference
    private Approver approver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApprovalStatusId")
    private DeptApprovalStatus approvalStatus;

    @Column(name = "ApprovalDate")
    private Timestamp approvalDate;

    @NotNull
    @Column(name = "Sequence", nullable = false)
    private Integer sequence;

    @Size(max = 2000)
    @Nationalized
    @Column(name = "Comment", length = 2000)
    private String comment;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreatedBy", nullable = false, length = 50)
    private String createdBy;

    @NotNull
    @Column(name = "CreatedDate", nullable = false)
    private Timestamp createdDate;

    @Size(max = 50)
    @Column(name = "UpdatedBy", length = 50)
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

}