package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class TenantSectionDetailWatchKey implements Serializable {

    @Column(name = "TenantSectionDetailId")
    private Long tenantSectionDetailId;

    @Column(name = "Sequence")
    private Integer sequence;

    public TenantSectionDetailWatchKey(Long tenantSectionDetailId, Integer sequence) {
        this.tenantSectionDetailId = tenantSectionDetailId;
        this.sequence = sequence;
    }

    public TenantSectionDetailWatchKey() {}
}
