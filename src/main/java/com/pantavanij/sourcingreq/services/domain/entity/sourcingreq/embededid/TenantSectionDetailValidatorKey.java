package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.*;

import javax.persistence.*;
import java.io.*;

@Embeddable
@Data
public class TenantSectionDetailValidatorKey implements Serializable {

    public TenantSectionDetailValidatorKey(Long tenantSectionDetailId, Integer validatorId) {
        this.tenantSectionDetailId = tenantSectionDetailId;
        this.validatorId = validatorId;
    }

    public TenantSectionDetailValidatorKey() {}

    @Column(name = "TenantSectionDetailId")
    private Long tenantSectionDetailId;

    @Column(name = "ValidatorId")
    private Integer validatorId;
}
