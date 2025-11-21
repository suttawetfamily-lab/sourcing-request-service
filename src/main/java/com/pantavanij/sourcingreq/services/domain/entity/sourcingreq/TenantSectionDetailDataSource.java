package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantSectionDetailDataSourceKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantSectionDetailDataSource")
public class TenantSectionDetailDataSource {

    @EmbeddedId
    private TenantSectionDetailDataSourceKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantSectionDetailId")
    @JoinColumn(name = "TenantSectionDetailId")
    @JsonBackReference
    private TenantSectionDetail tenantSectionDetail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("dataSourceId")
    @JoinColumn(name = "DataSourceId")
    @JsonBackReference
    private DataSource dataSource;

}
