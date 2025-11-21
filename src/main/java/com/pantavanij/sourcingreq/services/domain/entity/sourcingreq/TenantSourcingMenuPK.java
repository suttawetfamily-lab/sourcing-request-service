package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;

public class TenantSourcingMenuPK implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "SourcingMenuId")
    @JsonBackReference
    private SourcingMenu sourcingMenu;

}
