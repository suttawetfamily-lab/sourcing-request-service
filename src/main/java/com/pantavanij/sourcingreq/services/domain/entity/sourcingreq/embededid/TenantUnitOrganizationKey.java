package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TenantUnitOrganizationKey implements Serializable {

    @Column(name = "TenantId")
    private Integer tenantId;

    @Column(name = "UnitId")
    private Integer unitId;

    @Column(name = "OrganizationId")
    private Integer organizationId;
}
