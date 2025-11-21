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
@Table(name = "RequestAdditional")
public class RequestAdditional {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @Column(name = "WhtAbsorbedBy")
    private String whtAbsorbedBy;

    @Column(name = "EvaluationDate")
    private Timestamp evaluationDate;

    @Column(name = "PerformanceEvaluation")
    private String performanceEvaluation;

    @Column(name = "PdpaQ01")
    private String pdpaQ01;

    @Column(name = "PdpaQ02")
    private String pdpaQ02;

    @Column(name = "PdpaQ03")
    private String pdpaQ03;

    @Column(name = "PdpaQ04")
    private String pdpaQ04;

    @Column(name = "PdpaQ05")
    private String pdpaQ05;

    @Column(name = "PdpaQ06")
    private String pdpaQ06;

    @Column(name = "RelatePdpa")
    private String relatePdpa;

    @Column(name = "ThirdPartyRole")
    private String thirdPartyRole;

    @Column(name = "DpaType")
    private String dpaType;

    @Column(name = "OutsourceService")
    private String outsourceService;

    @Column(name = "Background")
    private String background;

    @Column(name = "RequestStatus")
    private String requestStatus;

    @Column(name = "Department")
    private String department;

    @Column(name = "ApproveNo")
    private String approveNo;

    @Column(name = "Costcenter")
    private String costcenter;

    @Column(name = "MakingContract")
    private String makingContract;

    @Column(name = "MakingRptContract")
    private String makingRptContract;

    @Column(name = "NeedWhtCert")
    private String needWhtCert;

    @Column(name = "VatAbsorbedBy")
    private String vatAbsorbedBy;

    @Column(name = "StampDuty")
    private String stampDuty;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    @Column(name = "MakingContractReason")
    private String makingContractReason;

    @Column(name = "MakingRptContractReason")
    private String makingRptContractReason;
}
