package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;

public class TenantSectionDetailValidatorPK implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantSectionDetailId")
    @JsonBackReference
    private TenantSectionDetail tenantSectionDetail;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ValidatorId")
    @JsonBackReference
    private Validator validator;

}
