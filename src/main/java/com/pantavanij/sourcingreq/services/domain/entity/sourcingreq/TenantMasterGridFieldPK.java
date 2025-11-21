package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import javax.persistence.*;
import java.io.Serializable;

public class TenantMasterGridFieldPK implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TenantId")
    private Tenant tenant;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MasterGridFieldId")
    private MasterGridField masterGridField;
}
