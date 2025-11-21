package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantUnitOrganizationKey;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "TenantUnitOrganization")
public class TenantUnitOrganization {

    @EmbeddedId
    private TenantUnitOrganizationKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantId")
    @JoinColumn(name = "TenantId")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("unitId")
    @JoinColumn(name = "UnitId")
    private Unit unit;

}

