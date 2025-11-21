// TenantCurrencyOrganizationPK.java
package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class TenantCurrencyOrganizationPK implements Serializable {
    @Column(name = "TenantId", nullable = false)
    private Integer tenantId;

    @Column(name = "CurrencyId", nullable = false)
    private Integer currencyId;

    @Column(name = "OrganizationId", nullable = false)
    private Integer organizationId;
}
