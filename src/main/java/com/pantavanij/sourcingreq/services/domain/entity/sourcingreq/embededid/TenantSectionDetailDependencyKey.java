package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class TenantSectionDetailDependencyKey implements Serializable {

    public TenantSectionDetailDependencyKey(Long tenantSectionDetailId, Integer sequence) {
        this.tenantSectionDetailId = tenantSectionDetailId;
        this.sequence = sequence;
    }

    public TenantSectionDetailDependencyKey() {}

    @Column(name = "TenantSectionDetailId")
    private Long tenantSectionDetailId;

    @Column(name = "Sequence")
    private Integer sequence;
}
