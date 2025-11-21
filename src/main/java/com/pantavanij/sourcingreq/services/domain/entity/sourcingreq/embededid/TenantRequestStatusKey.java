package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class TenantRequestStatusKey implements Serializable {
    @Column(name = "TenantId")
    private Long tenantId;

    @Column(name = "RequestStatusId")
    private Integer requestStatusId;
}
