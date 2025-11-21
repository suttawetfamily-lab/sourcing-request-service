package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class TenantSectionDetailDataSourceKey implements Serializable {

    public TenantSectionDetailDataSourceKey(Long tenantSectionDetailId, Integer dataSourceId) {
        this.tenantSectionDetailId = tenantSectionDetailId;
        this.dataSourceId = dataSourceId;
    }

    public TenantSectionDetailDataSourceKey(){}

    @Column(name = "TenantSectionDetailId")
    private Long tenantSectionDetailId;

    @Column(name = "DataSourceId")
    private Integer dataSourceId;
}
