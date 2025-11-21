package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "TenantSourcingMenu")
@IdClass(TenantSourcingMenuPK.class)
public class TenantSourcingMenu {
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
    
    @Column(name = "Visibled")
    private boolean visibled;
    
    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

}
