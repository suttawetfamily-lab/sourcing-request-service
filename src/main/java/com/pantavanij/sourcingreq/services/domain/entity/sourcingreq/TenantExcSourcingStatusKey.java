package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TenantExcSourcingStatusKey implements Serializable {

    @Column(name = "TenantId")
    private Integer tenantId;

    @Column(name = "ExcSourcingStatusId")
    private Integer excSourcingStatusId;
}
